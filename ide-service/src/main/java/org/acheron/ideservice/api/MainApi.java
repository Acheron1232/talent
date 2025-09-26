package org.acheron.ideservice.api;

import lombok.RequiredArgsConstructor;
import org.acheron.ideservice.service.ApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainApi {
    private final ApiService apiService;


    @GetMapping
    public String index() {

        String code = """
                #include <stdio.h>
                
                int main(void) {
                  char name[10];
                  scanf("%s", name);
                  printf("hello, %s\\n", name);
                  return 0;
                }
                """;

        String stdin = "Artem";

        String result = apiService.execute(code, stdin);

        return result;
    }


}
