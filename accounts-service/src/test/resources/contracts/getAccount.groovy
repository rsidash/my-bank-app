import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return account by login"
    request {
        method GET()
        url "/accounts/ivanov"
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            login: "ivanov",
            name: "Иванов Иван",
            birthdate: "2001-01-01",
            sum: 100
        )
    }
}
