package com.ifountain.opsgenie.client.cli.commands;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.ifountain.client.ClientConstants;
import com.ifountain.client.opsgenie.IOpsGenieClient;
import com.ifountain.client.opsgenie.model.alert.FileAttachRequest;
import com.ifountain.client.util.Strings;

import java.io.File;
import java.util.List;

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: 6/4/12
 * Time: 9:33 AM
 */
@Parameters(commandDescription = "Attaches files to an alert.")
public class AttachCommand extends BaseCommand{
    @ParametersDelegate
    private CommonCommandOptions commonOptions = new CommonCommandOptions();

    @Parameter(names = "--" + ClientConstants.API.ALERT_ID, description = "Id of the alert that the file will be attached. Either this or alias should be given.", splitter = NullSplitter.class)
    private String alertId;

    @Parameter(names = "--" + ClientConstants.API.ALIAS, description = "Alias of the alert that the file will be attached. Either this or alertId should be given.", variableArity = true, splitter = NullSplitter.class)
    private List<String> alias;

    @Parameter(names = "--" + ClientConstants.API.ATTACHMENT, description = "Path to the file.", variableArity = true, splitter = NullSplitter.class)
    private List<String> attachment;

    @Parameter(names = "--" + ClientConstants.API.INDEX_FILE, description = "", variableArity = true, splitter = NullSplitter.class, hidden = true)
    private List<String> indexFile;

    @Parameter(names = "--" + ClientConstants.API.NOTE, description = "User note.", variableArity = true, splitter = NullSplitter.class)
    private List<String> note;

    @Parameter(names = "--" + ClientConstants.API.SOURCE, description = "Source of acknowledge action.", variableArity = true, splitter = NullSplitter.class)
    private List<String> source;

    public AttachCommand(JCommander commander) {
        super(commander);
    }

    @Override
    public String getName() {
        return "attachFile";
    }

    @Override
    public void doExecute(IOpsGenieClient opsGenieClient) throws Exception {
        FileAttachRequest request = new FileAttachRequest();
        request.setApiKey(commonOptions.getApiKey());
        request.setAlertId(alertId);
        if (alias != null) request.setAlias(Strings.join(alias, " "));
        if (indexFile != null) request.setIndexFile(Strings.join(indexFile, " "));
        if (source != null) request.setSource(Strings.join(source, " "));
        if (commonOptions.getUser() != null) request.setUser(commonOptions.getUser());
        if (attachment != null) request.setFile(new File(Strings.join(attachment, " ")));
        if (note != null) request.setNote(Strings.join(note, " "));
        opsGenieClient.alert().attach(request);
    }

    @Override
    protected CommonCommandOptions getCommonCommandOptions() {
        return commonOptions;
    }
}
