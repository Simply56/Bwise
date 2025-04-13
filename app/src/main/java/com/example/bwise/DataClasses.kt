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

    // =========== Responses ============
    data class GetUserGroupsResponse(
        val groups: List<Group>
    )

    data class CreateGroupResponse(
        val group: Group,
    )

    data class JoinGroupResponse(
        val group: Group,
    )

    data class DeleteGroupResponse(
        val error: String
    )

    data class LoginResponse(
        val username: String,
    )

    data class AddExpenseResponse(
        val message: String,
        val amount: Double,
        val share_per_member: Double,
        val group: Group
    )

    data class GetDebtsResponse(
        val username: String,
        val group_name: String,
        val debts: List<Debt>
    )
}