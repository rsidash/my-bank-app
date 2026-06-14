import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should deposit money to account"
    request {
        method POST()
        url "/accounts/ivanov/deposit"
        headers {
            contentType applicationJson()
        }
        body(amount: 50)
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            login: "ivanov",
            name: "Иванов Иван",
            sum: 150
        )
    }
}
