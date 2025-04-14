package com.example.bwise

class DataClasses {

    // ========== Generic ===============
    data class Group(
        val creator: String,
        val members: List<String>,
        val name: String,
        val transactions: List<Transaction>
    )

    data class Transaction(
        val from_user: String,
        val to_user: String,
        val amount: Double,
    )

    data class Debt(
        val username: String,
        val amount: Double,
        val status: String
    )

    //============== Requests ================
    data class LoginRequest(
        val username: String,
    )

    data class GetUserGroupsRequest(
        val username: String
    )

    data class CreateGroupRequest(
        val username: String,
        val group_name: String,
    )

    data class JoinGroupRequest(
        val username: String,
        val group_name: String,
    )

    data class DeleteGroupRequest(
        val username: String,
        val group_name: String,
    )


    data class AddExpenseRequest(
        val username: String,
        val group_name: String,
        val amount: Double,
    )

    data class GetDebtsRequest(
        val username: String,
        val group_name: String
    )

    data class SettleUpRequest(
        val username: String,
        val group_name: String,
        val to_user: String
    )

    // =========== Responses ============

    open class BaseResponse(message: String)

    class GetUserGroupsResponse(
        message: String,
        val groups: List<Group>
    ) : BaseResponse(message)

    class CreateGroupResponse(
        message: String,
        val group: Group,
    ) : BaseResponse(message)

    class JoinGroupResponse(
        message: String,
        val group: Group,
    ) : BaseResponse(message)

    class DeleteGroupResponse(
        message: String,
    ) : BaseResponse(message)

    class LoginResponse(
        message: String,
        val username: String,
    ) : BaseResponse(message)

    class AddExpenseResponse(
        message: String,
        val amount: Double,
        val share_per_member: Double,
        val group: Group,
    ) : BaseResponse(message)

    class GetDebtsResponse(
        message: String,
        val username: String,
        val group_name: String,
        val debts: List<Debt>
    ) : BaseResponse(message)

    class SettleUpResponse(
        message: String,
        val group: Group,
        val transactions_settled: Int
    ) : BaseResponse(message)
}