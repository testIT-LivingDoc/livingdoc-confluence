package info.novatec.testit.livingdoc.confluence.actions.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.RepositoryType;
import info.novatec.testit.livingdoc.server.domain.component.ContentType;


@SuppressWarnings("serial")
public class FileSystemRegistration extends LivingDocServerAction {

    private static RepositoryType FILE = RepositoryType.newInstance("FILE");
    private List<Repository> fileRepositories;
    private Repository newRepository;
    private String repositoryUid;
    private String newName;
    private String newBaseTestUrl;
    private String newProjectName;
    private boolean editMode;
    private LinkedList<Project> projects;

    public String doGetFileSystemRegistration() {
        if ( ! isServerReady()) {
            addActionError(ConfluenceLivingDoc.SERVER_NOCONFIGURATION);
            return SUCCESS;
        }

        try {
            setFileRepositories(getService().getAllSpecificationRepositories());
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return SUCCESS;
    }

    public String doAddFileSystem() {
        try {
            setFileRepositories(getService().getAllSpecificationRepositories());

            if ( ! pathAlreadyExists()) {
                getNewRepository().setProject(Project.newInstance(newProjectName));
                newRepository.setType(FILE);
                newRepository.setName(newName);
                newRepository.setContentType(ContentType.TEST);
                newRepository.setBaseUrl(newBaseTestUrl);
                newRepository.setBaseRepositoryUrl(newBaseTestUrl);
                newRepository.setBaseTestUrl(newBaseTestUrl);

                getService().registerRepository(newRepository);
                newRepository = null;
            }
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return doGetFileSystemRegistration();
    }

    public String doRemoveFileSystem() {
        try {
            getService().removeRepository(repositoryUid);
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return doGetFileSystemRegistration();
    }

    public List<Repository> getFileRepositories() {
        if (fileRepositories != null) {
            try {
                setFileRepositories(getService().getAllSpecificationRepositories());
            } catch (LivingDocServerException e) {
                addActionError(e.getId());
            }
        }
        return fileRepositories;
    }

    public void setFileRepositories(List<Repository> repositories) {
        fileRepositories = new ArrayList<Repository>();
        for (Repository repository : repositories)
            if (repository.getType().equals(FILE))
                fileRepositories.add(repository);
    }

    public String getRepositoryUid() {
        return repositoryUid;
    }

    public void setRepositoryUid(String repositoryUid) {
        this.repositoryUid = repositoryUid;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getNewBaseTestUrl() {
        return newBaseTestUrl;
    }

    public void setNewBaseTestUrl(String newBaseTestUrl) {
        newBaseTestUrl = newBaseTestUrl.trim();
        if ( ! newBaseTestUrl.endsWith("/"))
            newBaseTestUrl += "/";

        this.newBaseTestUrl = newBaseTestUrl;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName.trim();
    }

    @Override
    public String getProjectName() {
        return newProjectName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.newProjectName = projectName;
    }

    public Repository getNewRepository() {
        if (newRepository != null)
            return newRepository;
        String uid = ldUtil.getSettingsManager().getGlobalSettings().getSiteTitle() + "-" + getProjectName() + "-F"
            + fileRepositories.size();
        newRepository = Repository.newInstance(uid);
        return newRepository;
    }

    private boolean pathAlreadyExists() throws LivingDocServerException {
        if (fileRepositories == null) {
            setFileRepositories(getService().getAllSpecificationRepositories());
        }

        for (Repository repo : fileRepositories) {
            if (repo.getBaseTestUrl().equals(newBaseTestUrl)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LinkedList<Project> getProjects() {
        if (projects != null)
            return projects;

        try {
            projects = new LinkedList<Project>(getService().getAllProjects());
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return projects;
    }

}
