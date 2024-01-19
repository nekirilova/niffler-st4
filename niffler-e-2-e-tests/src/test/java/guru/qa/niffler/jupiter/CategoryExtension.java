package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.CategoryApi;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(CategoryExtension.class);

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    ).build();

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .client(HTTP_CLIENT)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final CategoryApi categoryApi = RETROFIT.create(CategoryApi.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<GenerateCategory> category = AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(),
                GenerateCategory.class);


        if(category.isPresent()) {
            GenerateCategory categoryData = category.get();
            String categoryName = categoryData.category();
            List<CategoryJson> allCategories = categoryApi.getCategories(categoryData.username()).execute().body();
            if (allCategories != null) {
                for (CategoryJson existedCategory : allCategories) {
                    if (existedCategory.category().equals(categoryName)) {
                        System.out.println("Category is already exist");
                    } else {
                        CategoryJson categoryJson = new CategoryJson(
                                null,
                                categoryData.username(),
                                categoryData.category()
                        );
                        CategoryJson createdCategory = categoryApi.addCategory(categoryJson).execute().body();
                        extensionContext.getStore(NAMESPACE).put("category", createdCategory);
                    }
                }
            } else {
                CategoryJson categoryJson = new CategoryJson(
                        null,
                        categoryData.username(),
                        categoryData.category()
                );
                CategoryJson createdCategory = categoryApi.addCategory(categoryJson).execute().body();
                extensionContext.getStore(NAMESPACE).put("category", createdCategory);
            }
        }
    }
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CategoryExtension.NAMESPACE).get("category", CategoryJson.class);
    }

}
