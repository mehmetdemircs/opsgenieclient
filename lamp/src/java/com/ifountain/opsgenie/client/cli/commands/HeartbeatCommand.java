package com.ifountain.opsgenie.client.cli.commands;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.ifountain.client.ClientConstants;
import com.ifountain.client.opsgenie.IOpsGenieClient;
import com.ifountain.client.opsgenie.model.customer.HeartbeatRequest;
import com.ifountain.client.opsgenie.model.customer.HeartbeatResponse;
import com.ifountain.client.util.Strings;

import java.util.List;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: 6/4/12
 * Time: 9:46 AM
 */
@Parameters(commandDescription = "Sends heartbeat to Opsgenie.")
public class HeartbeatCommand extends BaseCommand{
    @Parameter(names = "--" + ClientConstants.API.SOURCE, description = "Source of action.", variableArity = true, splitter = NullSplitter.class)
    private List<String> source;

    @ParametersDelegate
    private CommonCommandOptions commonOptions = new CommonCommandOptions();

    public HeartbeatCommand(JCommander commander) {
        super(commander);
    }

    @Override
    public String getName() {
        return "heartbeat";
    }

    @Override
    public void doExecute(IOpsGenieClient opsGenieClient) throws Exception {
        HeartbeatRequest request = new HeartbeatRequest();
        request.setApiKey(commonOptions.getApiKey());
        if (source != null) request.setSource(Strings.join(source, " "));
        HeartbeatResponse response = opsGenieClient.heartbeat(request);
        System.out.println("heartbeat=" + response.getHeartbeat());
    }

    @Override
    protected CommonCommandOptions getCommonCommandOptions() {
        return commonOptions;
    }
}
