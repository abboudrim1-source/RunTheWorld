package com.runtheworld.presentation.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runtheworld.domain.model.FriendRequest
import com.runtheworld.domain.model.FriendUser
import com.runtheworld.domain.repository.FriendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FriendsState(
    val query: String = "",
    val searchResults: List<FriendUser> = emptyList(),
    val friends: List<FriendUser> = emptyList(),
    val inbox: List<FriendRequest> = emptyList(),
    val friendUids: Set<String> = emptySet(),
    val sentPendingUids: Set<String> = emptySet(),
    val inboxCount: Int = 0,
    val isSearching: Boolean = false
)

class FriendsViewModel(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FriendsState())
    val state: StateFlow<FriendsState> = _state.asStateFlow()

    /** Load everything needed for the profile screen and map badge. */
    fun loadAll() {
        viewModelScope.launch {
            val friends = friendRepository.getFriends()
            val inbox = friendRepository.getInbox()
            val sentUids = friendRepository.getSentPendingUids()
            _state.update {
                it.copy(
                    friends = friends,
                    friendUids = friends.map { f -> f.uid }.toSet(),
                    inbox = inbox,
                    inboxCount = inbox.size,
                    sentPendingUids = sentUids
                )
            }
        }
    }

    /** Lightweight reload just for the map badge count. */
    fun loadInboxCount() {
        viewModelScope.launch {
            val count = friendRepository.getPendingInboxCount()
            _state.update { it.copy(inboxCount = count) }
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    fun onQueryChange(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(query = query, searchResults = emptyList(), isSearching = false) }
            return
        }
        _state.update { it.copy(query = query, isSearching = true) }
        viewModelScope.launch {
            val results = friendRepository.searchUsers(query)
            _state.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    // ── Send request ──────────────────────────────────────────────────────────

    fun sendRequest(user: FriendUser) {
        viewModelScope.launch {
            friendRepository.sendFriendRequest(user.uid)
            _state.update { it.copy(sentPendingUids = it.sentPendingUids + user.uid) }
        }
    }

    // ── Inbox actions ─────────────────────────────────────────────────────────

    fun acceptRequest(request: FriendRequest) {
        viewModelScope.launch {
            friendRepository.acceptRequest(request.id)
            _state.update { s ->
                val newInbox = s.inbox.filter { it.id != request.id }
                s.copy(
                    inbox = newInbox,
                    inboxCount = newInbox.size,
                    friends = s.friends + request.sender,
                    friendUids = s.friendUids + request.sender.uid
                )
            }
        }
    }

    fun declineRequest(request: FriendRequest) {
        viewModelScope.launch {
            friendRepository.declineRequest(request.id)
            _state.update { s ->
                val newInbox = s.inbox.filter { it.id != request.id }
                s.copy(inbox = newInbox, inboxCount = newInbox.size)
            }
        }
    }
}
