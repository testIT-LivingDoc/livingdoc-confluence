package info.novatec.testit.livingdoc.confluence.actions.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import org.apache.commons.lang3.StringUtils;

import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;

import info.novatec.testit.livingdoc.confluence.LivingDocServerConfiguration;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.ClasspathSet;
import info.novatec.testit.livingdoc.server.domain.Runner;


/**
 * Action for the <code>test IT - LivingDoc Server Properties</code>.
 * <p></p>
 * Copyright (c) 2006 Pyxis technologies inc. All Rights Reserved.
 * 
 * @author JCHUET
 */
@SuppressWarnings("serial")
public class ConfigurationAction extends LivingDocServerAction {
    private static final String NONE_SELECTED = null;
    private static String ENV_TYPE_DEFAULT = "JAVA";

    private List<Runner> runners;
    private Runner selectedRunner;
    private String selectedRunnerName;
    private String classpath;

    private String newRunnerName = "";
    private String newCmdLineTemplate = "";
    private String newMainClass;
    private String newServerName;
    private String newServerPort;
    private String newEnvType = ENV_TYPE_DEFAULT;

    private boolean secured;
    private boolean addMode;
    private boolean editMode = false;
    private boolean editPropertiesMode;
    private boolean editClasspathsMode;

    public ConfigurationAction(LivingDocConfluenceManager confluenceLivingDoc) {
        super(confluenceLivingDoc);
    }
    public ConfigurationAction(){}

    public List<Space> getSpaces() {
        // NT - Fix for getting all Spaces
        ListBuilder<Space> lbGlobalSpace =
            getLivingDocConfluenceManager().getSpaceManager().getSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        return lbGlobalSpace.getRange(0, lbGlobalSpace.getAvailableSize() - 1);
    }

    public String getConfiguration() {
        if (isServerReady()) {
            doGetRunners();
        }
        return SUCCESS;
    }

    public String testConnection() {
        return SUCCESS;
    }

    public String updateConfiguration() {
        if (isServerReady()) {
            doGetRunners();
        }
        return SUCCESS;
    }

    public String verifyServerReady() {
        if ( ! isServerReady()) {
            addActionError(LivingDocConfluenceManager.SERVER_NOCONFIGURATION);
        }

        return SUCCESS;
    }

    public String doGetRunners() {
        if ( ! isServerReady()) {
            addActionError(LivingDocConfluenceManager.SERVER_NOCONFIGURATION);
            return SUCCESS;
        }

        try {
            runners = getPersistenceService().getAllRunners();
            if ( ! StringUtils.isEmpty(selectedRunnerName)) {
                for (Runner runner : runners) {
                    if (runner.getName().equals(selectedRunnerName)) {
                        selectedRunner = runner;
                        return SUCCESS;
                    }
                }
            }

            editPropertiesMode = false;
            editClasspathsMode = false;
            selectedRunnerName = NONE_SELECTED;
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return SUCCESS;
    }

    public String doAddRunner() {
        try {
            selectedRunner = Runner.newInstance(newRunnerName);
            selectedRunner.setServerName(newServerName);
            selectedRunner.setServerPort(newServerPort);
            selectedRunner.setSecured(secured);
            selectedRunner.setClasspaths(ClasspathSet.parse(classpath));

            getPersistenceService().createRunner(selectedRunner);
            successfullAction();
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return doGetRunners();
    }

    public String doUpdateRunner() {
        try {
            selectedRunner = getPersistenceService().getRunner(selectedRunnerName);
            selectedRunner.setName(newRunnerName);
            selectedRunner.setServerName(newServerName);
            selectedRunner.setServerPort(newServerPort);
            selectedRunner.setSecured(secured);
            selectedRunner.setClasspaths(ClasspathSet.parse(classpath));

            getPersistenceService().updateRunner(selectedRunnerName, selectedRunner);
            successfullAction();

            return doGetRunners();
        } catch (LivingDocServerException e) {
            try {
                runners = getPersistenceService().getAllRunners();
                selectedRunner.setName(selectedRunnerName);
            } catch (LivingDocServerException e1) {
                addActionError(e1);
            }

            addActionError(e);
        }

        return SUCCESS;
    }

    public String doRemoveRunner() {
        try {
            getPersistenceService().removeRunner(selectedRunnerName);
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return doGetRunners();
    }

    public String doEditClasspath() {
        try {
            selectedRunner = getPersistenceService().getRunner(selectedRunnerName);
            selectedRunner.setClasspaths(ClasspathSet.parse(classpath));
            getPersistenceService().updateRunner(selectedRunnerName, selectedRunner);
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return doGetRunners();
    }

    public Set<String> getClasspaths() {
        Set<String> classpaths = selectedRunner.getClasspaths();
        return classpaths == null ? new HashSet<String>() : classpaths;
    }

    public String getClasspathsAsTextAreaContent() {
        StringBuilder sb = new StringBuilder();
        for (String path : getClasspaths()) {
            sb.append(path);
            sb.append("\r");
        }
        return sb.toString();
    }

    public String getClasspathTitle() {
        return getText("livingdoc.runners.classpath");
    }

    public List<Runner> getRunners() {
        return runners;
    }

    public String getSelectedRunnerName() {
        return selectedRunnerName;
    }

    public void setSelectedRunnerName(String selectedRunnerName) {
        this.selectedRunnerName = selectedRunnerName.equals("none") ? null : selectedRunnerName;
    }

    public boolean isAddMode() {
        return addMode;
    }

    public void setAddMode(boolean addMode) {
        this.addMode = addMode;
    }

    public boolean isEditPropertiesMode() {
        return editPropertiesMode;
    }

    public void setEditPropertiesMode(boolean editPropertiesMode) {
        this.editPropertiesMode = editPropertiesMode;
    }

    public boolean isEditClasspathsMode() {
        return editClasspathsMode;
    }

    public void setEditClasspathsMode(boolean editClasspathsMode) {
        this.editClasspathsMode = editClasspathsMode;
    }

    public Runner getSelectedRunner() {
        return selectedRunner;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath.trim();
    }

    public String getNewMainClass() {
        return newMainClass;
    }

    public String getNewRunnerName() {
        return newRunnerName;
    }

    public void setNewRunnerName(String newRunnerName) {
        this.newRunnerName = newRunnerName.trim();
    }

    public String getNewServerName() {
        return newServerName;
    }

    public void setNewServerName(String newServerName) {
        this.newServerName = newServerName.trim();
    }

    public String getNewServerPort() {
        return newServerPort;
    }

    public void setNewServerPort(String newServerPort) {
        this.newServerPort = newServerPort.trim();
    }

    public String getNewEnvType() {
        return newEnvType;
    }

    public void setNewEnvType(String newEnvType) {
        this.newEnvType = newEnvType;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }

    public String getNewCmdLineTemplate() {
        return newCmdLineTemplate;
    }

    public String getExecutionTimeout() {
        return getLivingDocConfluenceManager().getLDServerConfigurationActivator().getConfiguration().getProperties().getProperty("executionTimeout");
    }

    public void setExecutionTimeout(String executionTimeout) {
        LivingDocServerConfiguration conf = getLivingDocConfluenceManager().getLDServerConfigurationActivator().getConfiguration();
        conf.getProperties().setProperty("executionTimeout", executionTimeout);
        getLivingDocConfluenceManager().getLDServerConfigurationActivator().storeConfiguration(conf);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    private void successfullAction() {
        addMode = false;
        editPropertiesMode = false;
        editClasspathsMode = false;
        secured = false;
        selectedRunnerName = newRunnerName;
        newRunnerName = "";
        newServerName = "";
        newServerPort = "";
        newEnvType = ENV_TYPE_DEFAULT;
        newCmdLineTemplate = "";
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }
}
