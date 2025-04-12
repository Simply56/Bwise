package com.example.bwise

class DataClasses {
    data class LoginRequest(
        val username: String,
    )

    data class LoginResponse(
        val username: String,
    )


    data class GetUserGroupsRequest(
        val username: String
    )


    data class GetUserGroupsResponse(
        val groups: List<Group>
    )


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

    data class CreateGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class CreateGroupResponse(
        val group: Group,
    )


    data class JoinGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class JoinGroupResponse(
        val group: Group,
    )


    data class DeleteGroupRequest(
        val group_name: String,
        val username: String,
    )

    data class DeleteGroupResponse(
        val error: String
    )
}