package guru.qa.niffler.jupiter;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import okhttp3.OkHttpClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class IssueExtension implements ExecutionCondition {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(IssueExtension.class);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();
    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .client(HTTP_CLIENT)
            .baseUrl(" https://api.github.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final GhApi ghApi = RETROFIT.create(GhApi.class);
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
      DisabledByIssue disabledByIssue = AnnotationSupport.findAnnotation(
            extensionContext.getRequiredTestMethod(),
                DisabledByIssue.class
        ).orElse(
                AnnotationSupport.findAnnotation(
                        extensionContext.getRequiredTestClass(),
                        DisabledByIssue.class
                ).orElse(null)
        );

        if(disabledByIssue != null) {
            try {
                JsonNode responseBody = ghApi.issue(

                        "Bearer " + System.getenv("GH_TOKEN"),
                        disabledByIssue.value()
                ).execute().body();
                if (responseBody != null) {
                    return "open".equals(responseBody.get("state").asText())
                            ? ConditionEvaluationResult.disabled("Disabled by issue")
                            : ConditionEvaluationResult.enabled("Issue was closed");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ConditionEvaluationResult.enabled("Annotation wasn't found");
    }
}
