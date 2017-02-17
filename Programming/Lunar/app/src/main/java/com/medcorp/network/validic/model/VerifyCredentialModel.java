package com.medcorp.network.validic.model;

/**
 * Created by gaillysu on 16/3/9.
 */
public class VerifyCredentialModel {
    ValidicSummary summary;
    ValidicOrganization organization;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ValidicOrganization organization) {
        this.organization = organization;
    }
}
