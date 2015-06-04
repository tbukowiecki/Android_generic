package com.storefrontsdk.internal.data;

import com.storefrontsdk.Environment;
import com.storefrontsdk.internal.models.ProjectStore;

import org.androidannotations.annotations.EBean;

/**
 * Created by Kuba on 27/01/15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class DataContainer {

    protected ProjectStore projectStore;
    protected int projectId;
    protected int payPalLaunches = 0;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public ProjectStore getProjectStore() {
        return projectStore;
    }

    public void setProjectStore(ProjectStore projectStore) {
        this.projectStore = projectStore;
    }

    public int getPayPalLaunches() {
        return payPalLaunches;
    }

    public void setPayPalLaunches(int payPalLaunches) {
        this.payPalLaunches = payPalLaunches;
    }
}
