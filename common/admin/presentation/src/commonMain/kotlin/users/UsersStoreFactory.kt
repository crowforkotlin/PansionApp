package users

import AdminRepository
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.networkInterface.NetworkInterface
import components.cBottomSheet.CBottomSheetComponent
import users.UsersStore.Intent
import users.UsersStore.Label
import users.UsersStore.State

class UsersStoreFactory(
    private val storeFactory: StoreFactory,
    private val adminRepository: AdminRepository,
    private val nUsersInterface: NetworkInterface,
    private val eUserBottomSheet: CBottomSheetComponent,
    private val cUserBottomSheet: CBottomSheetComponent,
) {

    fun create(): UsersStore {
        return UsersStoreImpl()
    }

    private inner class UsersStoreImpl :
        UsersStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "UsersStore",
            initialState = UsersStore.State(),
            executorFactory = { UsersExecutor(
                adminRepository = adminRepository,
                nUsersInterface = nUsersInterface,
                eUserBottomSheet = eUserBottomSheet,
                cUserBottomSheet = cUserBottomSheet
            ) },
            reducer = UsersReducer
        )
}