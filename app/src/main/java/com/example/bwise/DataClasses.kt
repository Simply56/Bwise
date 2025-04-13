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


    data class GetUserGroupsResponse(
        val message: String,
        val groups: List<Group>
    )

    data class CreateGroupResponse(
        val message: String,
        val group: Group,
    )

    data class JoinGroupResponse(
        val message: String,
        val group: Group,
    )

    data class DeleteGroupResponse(
        val message: String,
        val error: String
    )

    data class LoginResponse(
        val message: String,
        val username: String,
    )

    data class AddExpenseResponse(
        val message: String,
        val amount: Double,
        val share_per_member: Double,
        val group: Group
    )

    data class GetDebtsResponse(
        val message: String,
        val username: String,
        val group_name: String,
        val debts: List<Debt>
    )

    data class SettleUpResponse(
        val message: String,
        val group: Group,
        val transactions_settled: Int // number of settled transactions
    )
}