package org.rzlabs.halo.curator;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

public class ExhibitorConfig {

    @JsonProperty
    private List<String> hosts = new ArrayList<>();

    @JsonProperty("port")
    @Min(0)
    @Max(0xffff)
    private int restPort = 8080;

    @JsonProperty
    private String restUriPath = "/exhibitor/v1/cluster/list";

    @JsonProperty
    private boolean useSsl = false;

    @JsonProperty
    @Min(0)
    private int pollingMs = 10000;

    public List<String> getHosts() {
        return hosts;
    }

    public int getRestPort() {
        return restPort;
    }

    public String getRestUriPath() {
        return restUriPath;
    }

    public boolean getUseSsl() {
        return useSsl;
    }

    public int getPollingMs() {
        return pollingMs;
    }
}
