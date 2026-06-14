import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return other accounts excluding current user"
    request {
        method GET()
        url "/accounts/ivanov/others"
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            [login: "petrov", name: "Петров Петр"]
        ])
    }
}
