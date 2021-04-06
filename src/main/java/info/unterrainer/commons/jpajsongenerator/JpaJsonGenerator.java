package info.unterrainer.commons.jpajsongenerator;

import java.util.List;

import info.unterrainer.commons.cliutils.Arg;
import info.unterrainer.commons.cliutils.Cli;
import info.unterrainer.commons.cliutils.CliParser;
import info.unterrainer.commons.cliutils.Flag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JpaJsonGenerator {

	private class LongOpt {
		public static final String SERVER = "server";
		public static final String USER = "user";
		public static final String PASSWORD = "password";
		public static final String LIST = "list";
		public static final String READ = "read";
		public static final String OUTPUT = "output";
		public static final String ROOT_NODE = "rootnode";
		public static final String RECURSIVE = "recursive";
		public static final String SUBSCRIPTION = "subscription";
		public static final String SECONDS = "seconds";
	}

	protected static final String SERVER = "opc.tcp://192.168.251.16:4980/Honeywell-OPCUA";
	protected static final String USER = "rsgroup";
	protected static final String PASSWORD = "R+SGroup2017";

	public static void main(final String[] args) {
		Cli cli = CliParser
				.cliFor(args, "OpcUaBrowser", "a small tool to help validate a few theories concerning OPC-UA")
				.addArg(Arg.String(LongOpt.SERVER)
						.shortName("s")
						.description("the server instance to connect to (opc.tcp://<ip>.<port>/name)")
						.defaultValue(SERVER))
				.addArg(Arg.String(LongOpt.USER)
						.shortName("u")
						.description("the user to use when connecting to the server")
						.defaultValue(USER))
				.addArg(Arg.String(LongOpt.PASSWORD)
						.shortName("p")
						.description("the password used when connecting to the server")
						.defaultValue(PASSWORD))
				.addFlag(Flag.builder(LongOpt.LIST)
						.shortName("l")
						.description("browses and lists all nodes of this server instance"))
				.addArg(Arg.String(LongOpt.ROOT_NODE)
						.description("the root node id to start browsing with (for -l option). "
								+ "The global root-node if string is not parseable or omitted"))
				.addFlag(Flag.builder(LongOpt.RECURSIVE).shortName("R").description("browse the nodes recursively"))
				.addArg(Arg.String(LongOpt.OUTPUT)
						.shortName("o")
						.description(
								"use to specifiy a file that the output will be saved to (overwrites file if already present)"))
				.addArg(Arg.String(LongOpt.READ)
						.shortName("r")
						.description("use to read the values (double) of the provided node-ids")
						.unlimited())
				.addArg(Arg.String(LongOpt.SUBSCRIPTION)
						.description("make a subscription for nodeId=ARGS and run it for 10 (or <duration>) seconds")
						.unlimited())
				.addArg(Arg.Integer(LongOpt.SECONDS)
						.description("if you make a subscription, this overrides the 10 seconds duration")
						.defaultValue(10)
						.optional())
				.addMinRequired(1, LongOpt.LIST, LongOpt.READ, LongOpt.SUBSCRIPTION)
				.create();
		if (cli.isHelpSet())
			System.exit(0);
		String endpointUrl = cli.getArgValue(LongOpt.SERVER);
		String user = cli.getArgValue(LongOpt.USER);
		String password = cli.getArgValue(LongOpt.PASSWORD);

		// start browsing at root folder
		if (cli.isFlagSet(LongOpt.LIST)) {
			log.info("flag '{}' is set", LongOpt.LIST);

			if (cli.isArgSet(LongOpt.ROOT_NODE)) {
				String nodeId = cli.getArgValue(LongOpt.ROOT_NODE);
				log.info("arg '{}' is set to [{}]", LongOpt.ROOT_NODE, nodeId);
			}

			if (cli.isArgSet(LongOpt.OUTPUT)) {
				String file = cli.getArgValue(LongOpt.OUTPUT);
				log.info("Writing output JSON file to [{}]", file);
			}
		}
		if (cli.isFlagSet(LongOpt.READ)) {
			log.info("Reading values from given nodes");
			List<String> readIds = cli.getArgValues(LongOpt.READ);
		}
		if (cli.isArgSet(LongOpt.SUBSCRIPTION)) {
			log.info("Testing subscription with given nodes");
			List<String> subscriptionIds = cli.getArgValues(LongOpt.SUBSCRIPTION);
			int seconds = cli.getArgValue(LongOpt.SECONDS);
		}
	}
}