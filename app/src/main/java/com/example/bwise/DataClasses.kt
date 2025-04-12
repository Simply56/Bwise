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
        val amount: Int,
    )

    //============== Requests ================
    data class LoginRequest(
        val username: String,
    )

    data class GetUserGroupsRequest(
        val username: String
    )

    data class CreateGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class JoinGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class DeleteGroupRequest(
        val group_name: String,
        val username: String,
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
}