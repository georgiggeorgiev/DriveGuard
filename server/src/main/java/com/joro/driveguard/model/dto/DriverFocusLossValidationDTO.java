package com.joro.driveguard.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DriverFocusLossValidationDTO extends DriverFocusLossDTO
{
    private String APIKey;

    @JsonIgnore // Do not expose API key in JSON
    public String getAPIKey()
    {
        return this.APIKey;
    }

    @JsonProperty("APIKey") // Required for capital letter property
    public void setAPIKey(String APIKey)
    {
        this.APIKey = APIKey;
    }

    public boolean equals(final Object o)
    {
        if (o == this) return true;
        if (!(o instanceof DriverFocusLossValidationDTO)) return false;
        final DriverFocusLossValidationDTO other = (DriverFocusLossValidationDTO) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$APIKey = this.getAPIKey();
        final Object other$APIKey = other.getAPIKey();
        if (this$APIKey == null ? other$APIKey != null : !this$APIKey.equals(other$APIKey)) return false;
        return true;
    }

    protected boolean canEqual(final Object other)
    {
        return other instanceof DriverFocusLossValidationDTO;
    }

    public int hashCode()
    {
        final int PRIME = 59;
        int result = 1;
        final Object $APIKey = this.getAPIKey();
        result = result * PRIME + ($APIKey == null ? 43 : $APIKey.hashCode());
        return result;
    }

    public String toString()
    {
        return "DriverFocusLossValidationDTO(APIKey=" + this.getAPIKey() + ")";
    }
}
