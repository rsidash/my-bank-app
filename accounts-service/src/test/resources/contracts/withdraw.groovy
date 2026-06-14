import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should withdraw money from account"
    request {
        method POST()
        url "/accounts/ivanov/withdraw"
        headers {
            contentType applicationJson()
        }
        body(amount: 30)
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(
            login: "ivanov",
            name: "Иванов Иван",
            sum: 70
        )
    }
}
