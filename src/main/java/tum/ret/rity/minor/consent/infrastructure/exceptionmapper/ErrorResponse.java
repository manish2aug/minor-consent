package tum.ret.rity.minor.consent.infrastructure.exceptionmapper;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ErrorResponse {
    @JsonbProperty("error_msg")
    private Collection<String> errorMsg;
    private String referenceNumber;

    public ErrorResponse(Collection<String> errors, String referenceNumber) {
        this.referenceNumber = referenceNumber;
        this.errorMsg = new ArrayList<>();
        if (errors != null) errorMsg.addAll(errors);
    }
}