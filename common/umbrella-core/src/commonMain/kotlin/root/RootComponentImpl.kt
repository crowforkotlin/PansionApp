package root

//import mentors.MentorsComponent
//import students.StudentsComponent
import AuthRepository
import FIO
import SettingsComponent
import achievements.AdminAchievementsComponent
import achievements.HomeAchievementsComponent
import activation.ActivationComponent
import admin.AdminComponent
import allGroupMarks.AllGroupMarksComponent
import allGroupMarks.AllGroupMarksStore
import applicationVersion
import asValue
import cabinets.CabinetsComponent
import calendar.CalendarComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.webhistory.WebHistoryController
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import components.cAlertDialog.CAlertDialogStore
import components.networkInterface.NetworkInterface
import detailedStups.DetailedStupsComponent
import di.Inject
import dnevnikRuMarks.DnevnikRuMarksComponent
import formRating.FormRatingComponent
import groups.GroupsComponent
import home.HomeComponent
import home.HomeStore
import homeTasks.HomeTasksComponent
import journal.JournalComponent
import journal.JournalStore
import lessonReport.LessonReportComponent
import lessonReport.LessonReportComponent.Output
import lessonReport.LessonReportStore
import login.LoginComponent
import mentoring.MentoringComponent
import mentoring.MentoringStore
import ministry.MinistryComponent
import parents.AdminParentsComponent
import profile.ProfileComponent
import qr.QRComponent
import rating.RatingComponent
import rating.RatingStore
import root.RootComponent.Child
import root.RootComponent.Config
import root.store.RootStore
import root.store.RootStoreFactory
import schedule.ScheduleComponent
import school.SchoolComponent
import server.Moderation
import server.Roles
import studentLines.StudentLinesComponent
import users.UsersComponent
import kotlin.reflect.KClass


//avatarId = authRepository.fetchAvatarId(),
//            login = authRepository.fetchLogin(),
//            fio = FIO(
//                name = authRepository.fetchName(),
//                surname = authRepository.fetchSurname(),
//                praname = authRepository.fetchPraname(),
//            ),
//            role = authRepository.fetchRole()

@ExperimentalDecomposeApi
class RootComponentImpl(
    private var componentContext: ComponentContext, //idk 0_0 but works
    private val storeFactory: StoreFactory,
    override val secondLogin: String? = null,
    override val secondAvatarId: Int? = null,
    override val secondFIO: FIO? = null,
    override val isMentoring: Boolean? = null,
//    override val backHandler: BackHandler,
    private val firstScreen: Config = Config.AuthActivation,
    val onBackButtonPress: (() -> Unit)? = null,
//    override val stateKeeper: StateKeeper,
    deepLink: DeepLink = DeepLink.None,
//    private val path: String = "",
    private val webHistoryController: WebHistoryController? = null
) : RootComponent, ComponentContext by componentContext {
    override val stateKeeper: StateKeeper
        get() = componentContext.stateKeeper
    override val instanceKeeper: InstanceKeeper
        get() = componentContext.instanceKeeper
    private var authRepository: AuthRepository = Inject.instance()

    override val checkNInterface: NetworkInterface = NetworkInterface(
        componentContext,
        storeFactory,
        name = "CheckNInterfaceRoot"
    )

    private val rootStore =
        instanceKeeper.getStore {
            RootStoreFactory(
                storeFactory = storeFactory,
                isBottomBarShowing = isBottomBarShowing(),
                //              !!!!!! stack?
                currentScreen = stack?.value?.active?.configuration ?: getFirstScreen(),
                authRepository = authRepository,
                checkNInterface = checkNInterface,
                gotoHome = {
                    navigation.replaceAll(
                        Config.MainHome
//                            (
//                            avatarId = authRepository.fetchAvatarId(),
//                            login = authRepository.fetchLogin(),
//                            fio = FIO(
//                                name = authRepository.fetchName(),
//                                surname = authRepository.fetchSurname(),
//                                praname = authRepository.fetchPraname(),
//                            ),
//                            role = authRepository.fetchRole()
//                        )
                    )
                }
            ).create()
        }

    private fun isBottomBarShowing(): Boolean {
        //!!!!!! stack?
        return if (stack != null) stack.value.active.configuration in listOf(
            Config.MainHome,
            Config.MainAdmin,
//            Config.MainRating,
            Config.MainJournal,
            Config.MainSchool,
        ) else authRepository.isUserLoggedIn()
    }

    private fun getFirstScreen(): Config {
        // FIX WEB Uncaught (in promise) IllegalStateException: Configurations must be unique: [AuthActivation, AuthActivation]
        if (stack != null && stack.value.active.configuration == Config.AuthActivation) {
            navigation.replaceAll()
        }
        return firstScreen
//        (
//            login = secondLogin,
//            fio = secondFIO!!,
//            avatarId = secondAvatarId!!,
//            role = Roles.student
//        ) //if (authRepository.isUserLoggedIn()) Config.MainHome else
    }

    val backCallback = BackCallback {
        onBackClicked()
    }


    override fun onBackClicked() {
        when (val child = childStack.active.instance) {
            is Child.HomeSettings -> onHomeSettingsOutput(SettingsComponent.Output.Back)
            is Child.HomeStudentLines -> onHomeStudentLinesOutput(StudentLinesComponent.Output.Back)
            is Child.AdminCabinets -> onAdminCabinetsOutput(CabinetsComponent.Output.Back)
            is Child.AdminCalendar -> onAdminCalendarOutput(CalendarComponent.Output.Back)
            is Child.AdminGroups -> onAdminGroupsOutput(GroupsComponent.Output.Back)
            is Child.AdminSchedule -> onAdminScheduleOutput(ScheduleComponent.Output.Back)
            is Child.AdminUsers -> onAdminUsersOutput(UsersComponent.Output.Back)
            is Child.AuthActivation -> {}
            is Child.AuthLogin -> onLoginOutput(LoginComponent.Output.BackToActivation)
            is Child.HomeAllGroupMarks -> onAllGroupMarksOutput(AllGroupMarksComponent.Output.Back)
            is Child.HomeDetailedStups -> onDetailedStupsOutput(DetailedStupsComponent.Output.Back)
            is Child.HomeDnevnikRuMarks -> onDnevnikRuMarksOutput(DnevnikRuMarksComponent.Output.Back)
            is Child.HomeProfile -> onHomeProfileOutput(ProfileComponent.Output.Back)
            is Child.HomeTasks -> onHomeTasksOutput(HomeTasksComponent.Output.Back)
            is Child.LessonReport -> onLessonReportOutput(Output.Back)

            is Child.AdminAchievements -> onAdminAchievementsOutput(AdminAchievementsComponent.Output.Back)
            is Child.HomeAchievements -> onHomeAchievementsOutput(HomeAchievementsComponent.Output.Back)
//            else -> {
//                navigation.pop()
//            }
            is Child.AdminParents -> onAdminParentsOutput(AdminParentsComponent.Output.Back)
            is Child.MainAdmin -> navigation.pop()
            is Child.MainHome -> navigation.pop()
            is Child.MainJournal -> navigation.pop()
            is Child.MainMentoring -> navigation.pop()
            is Child.MainRating -> onRatingOutput(RatingComponent.Output.Back)
            is Child.MainSchool -> navigation.pop()
            is Child.QRScanner -> onQRScannerOutput(QRComponent.Output.Back)
            is Child.SchoolFormRating -> onFormRatingOutput(FormRatingComponent.Output.Back)
            is Child.SecondView -> navigation.pop()
            is Child.SchoolMinistry -> onMinistryOutput(MinistryComponent.Output.Back)
        }
    }

//    private fun getRoot(child: Child): Pair<RootComponent.RootCategories, Config> {
//        return when (child) {
//            is Child.MainAdmin -> Pair(Admin, Config.MainAdmin)
//            is Child.MainHome -> Pair(Home, Config.MainHome)
//            is Child.MainJournal -> Pair(Journal, Config.MainJournal)
//            is Child.MainRating -> Pair(Rating, Config.MainRating)
//            is Child.AdminCabinets -> Pair(Admin, Config.AdminCabinets)
//            else -> TODO()
//        }
//    }


    //    @OptIn(ExperimentalCoroutinesApi::class)
//    override val state: StateFlow<RootStore.State> = rootStore.stateFlow
    override val model = rootStore.asValue()
    private val navigation = StackNavigation<Config>()
    val stack = childStack(
        source = navigation,
        initialStack = {
            getInitialStack(
                webHistoryPaths = webHistoryController?.historyPaths,
                deepLink = deepLink
            )
        },
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::child,
        key = if (secondLogin == null) "MAIN" else "SECOND"
    )


    override val childStack: Value<ChildStack<*, Child>> = stack

    private var mainHomeComponent: HomeComponent? = null
    private var mainJournalComponent: JournalComponent? = null
    private var mainMentoringComponent: MentoringComponent? = null
    private var mainSchoolComponent: SchoolComponent? = null
    private var mainAdminComponent: AdminComponent? = null
    private var mainRatingComponent: RatingComponent? = null

    private fun getMainAdminComponent(
        componentContext: ComponentContext,
        getOld: Boolean = false
    ): AdminComponent {
        return if (getOld && mainAdminComponent != null) mainAdminComponent!! else {
            mainAdminComponent = AdminComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                output = ::onAdminOutput
            )
            mainAdminComponent!!
        }
    }

    private fun getMainJournalComponent(
        componentContext: ComponentContext,
        getOld: Boolean = false
    ): JournalComponent {
        return if (getOld && mainJournalComponent != null) mainJournalComponent!! else {
            mainJournalComponent = JournalComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                output = { onJournalOutput(it) }
            )

            mainJournalComponent!!
        }
    }

    private fun getMainMentoringComponent(
        componentContext: ComponentContext,
        getOld: Boolean = true
    ): MentoringComponent {
        return if (getOld && mainMentoringComponent != null) mainMentoringComponent!! else {
            mainMentoringComponent = MentoringComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                output = ::onMainMentoringOutput
            )
            mainMentoringComponent!!
        }
    }

    private fun getMainSchoolComponent(
        componentContext: ComponentContext,
        getOld: Boolean = true
    ): SchoolComponent {
        return if (getOld && mainSchoolComponent != null
//                   && mainSchoolComponent?.state?.value?.login == (secondLogin
//                ?: authRepository.fetchLogin()
//                           )
        ) mainSchoolComponent!! else {
            mainSchoolComponent = SchoolComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                output = ::onMainSchoolOutput,
                login = secondLogin ?: authRepository.fetchLogin(),
                role = if (secondLogin != null) Roles.student else authRepository.fetchRole(),
                moderation = if (secondLogin != null) Moderation.nothing else authRepository.fetchModeration(),
                isSecondScreen = secondLogin != null
            )
            mainSchoolComponent!!
        }
    }

    private fun getMainHomeComponent(
        componentContext: ComponentContext,
        getOld: Boolean = false
    ): HomeComponent {
        return if (getOld && mainHomeComponent != null) mainHomeComponent!! else {
            mainHomeComponent = HomeComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                journalComponent = getMainJournalComponent(
                    componentContext,
                ),
                output = ::onHomeOutput,
                avatarId = secondAvatarId ?: authRepository.fetchAvatarId(),
                login = secondLogin ?: authRepository.fetchLogin(),
                name = secondFIO?.name ?: authRepository.fetchName(),
                surname = secondFIO?.surname ?: authRepository.fetchSurname(),
                praname = secondFIO?.praname ?: authRepository.fetchPraname(),
                role = if (secondLogin == null) authRepository.fetchRole() else Roles.student,
                onBackButtonPress = onBackButtonPress,
                isParent = if (secondLogin == null) authRepository.fetchIsParent() else false,
                moderation = if (secondLogin == null) authRepository.fetchModeration() else Roles.nothing
            )
            mainHomeComponent!!
        }
    }

    private fun getMainRatingComponent(
        componentContext: ComponentContext,
        getOld: Boolean = false
    ): RatingComponent {
        return if (getOld && mainRatingComponent != null) mainRatingComponent!! else {
            mainRatingComponent = RatingComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                output = ::onRatingOutput,
                avatarId = secondAvatarId ?: authRepository.fetchAvatarId(),
                login = secondLogin ?: authRepository.fetchLogin(),
                fio = secondFIO ?: FIO(
                    name = authRepository.fetchName(),
                    surname = authRepository.fetchSurname(),
                    praname = authRepository.fetchPraname()
                )
            )
            mainRatingComponent!!
        }
    }

    private fun child(config: Config, childContext: ComponentContext): Child =
        when (config) {
            is Config.AuthLogin -> {
                Child.AuthLogin(
                    LoginComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onLoginOutput,
                        login = config.login
                    )
                )
            }

            is Config.AuthActivation -> {
                Child.AuthActivation(
                    ActivationComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onActivationOutput
                    )
                )
            }

            is Config.MainHome -> {
                Child.MainHome(
                    homeComponent = getMainHomeComponent(childContext, true),
                    journalComponent = mainHomeComponent!!.journalComponent!!,//getMainJournalComponent(componentContext),
                    ratingComponent = getMainRatingComponent(childContext, getOld = true)
                )
            }

            is Config.MainJournal -> {
                Child.MainJournal(
                    getMainHomeComponent(childContext, true),
                    getMainJournalComponent(childContext, true)
                )
            }

            is Config.MainAdmin -> {
                Child.MainAdmin(
                    getMainAdminComponent(childContext, true)
                )
            }

            is Config.AdminUsers -> {
                Child.AdminUsers(
                    adminComponent = getMainAdminComponent(childContext, true),
                    usersComponent = UsersComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onAdminUsersOutput
                    )
                )
            }

            Config.AdminGroups -> {
                println(config.toString())
                Child.AdminGroups(
                    adminComponent = getMainAdminComponent(childContext, true),
                    GroupsComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onAdminGroupsOutput
                    )
                )
            }

            is Config.LessonReport -> {
                Child.LessonReport(
                    lessonReport = LessonReportComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onLessonReportOutput,
                        reportData = config.reportData
                    ),
                    journalComponent = getMainJournalComponent(childContext, true)
                )
            }

            Config.HomeSettings -> {
                Child.HomeSettings(
                    SettingsComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onHomeSettingsOutput
                    )
                )
            }

            is Config.HomeDnevnikRuMarks -> {
                Child.HomeDnevnikRuMarks(
                    homeComponent = getMainHomeComponent(childContext, true),
                    dnevnikRuMarksComponent = DnevnikRuMarksComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onDnevnikRuMarksOutput,
                        studentLogin = config.studentLogin
                    )
                )
            }

            is Config.HomeDetailedStups -> {
                Child.HomeDetailedStups(
                    homeComponent = getMainHomeComponent(childContext, true),
                    detailedStups = DetailedStupsComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onDetailedStupsOutput,
                        studentLogin = config.studentLogin,
                        reason = config.reason,
                        avatarId = config.avatarId,
                        name = config.name
                    )
                )
            }

            is Config.HomeAllGroupMarks -> {
                Child.HomeAllGroupMarks(
                    journalComponent = getMainJournalComponent(childContext, true),
                    allGroupMarksComponent = AllGroupMarksComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onAllGroupMarksOutput,
                        groupId = config.groupId,
                        groupName = config.groupName,
                        subjectId = config.subjectId,
                        subjectName = config.subjectName,
                        login = config.teacherLogin
                    )
                )
            }

            is Config.HomeProfile -> {
                Child.HomeProfile(
                    homeComponent = getMainHomeComponent(childContext, true),
                    profileComponent = ProfileComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        studentLogin = config.studentLogin,
                        fio = config.fio,
                        avatarId = config.avatarId,
                        output = ::onHomeProfileOutput,
                        changeAvatarOnMain = {
                            mainHomeComponent?.onEvent(HomeStore.Intent.UpdateAvatarId(it))
                        },
                        isOwner = config.isOwner,
                        isCanEdit = config.isCanEdit
                    )
                )
            }

            is Config.AdminSchedule -> {
                Child.AdminSchedule(
                    scheduleComponent = ScheduleComponent(
                        childContext,
                        storeFactory,
                        output = ::onAdminScheduleOutput,
                        login = authRepository.fetchLogin(),
                        isCanBeEdited = config.isModerator
                    )
                )
            }

            Config.AdminCabinets -> {
                Child.AdminCabinets(
                    adminComponent = getMainAdminComponent(childContext, true),
                    cabinetsComponent = CabinetsComponent(
                        childContext,
                        storeFactory,
                        output = ::onAdminCabinetsOutput
                    )
                )
            }

            Config.MainRating -> {
                Child.MainRating(
                    schoolComponent = getMainSchoolComponent(childContext, true),
                    ratingComponent = getMainRatingComponent(childContext, true)
                )
            }

            is Config.HomeTasks -> {
                Child.HomeTasks(
                    homeComponent = getMainHomeComponent(childContext, true),
                    homeTasksComponent = HomeTasksComponent(
                        childContext,
                        storeFactory,
                        login = config.studentLogin,
                        avatarId = config.avatarId,
                        name = config.name,
                        output = ::onHomeTasksOutput,
                        updateHTCount = {
                            mainHomeComponent?.onEvent(HomeStore.Intent.UpdateHomeWorkEmoji(it))
                        }
                    )
                )
            }


            Config.AdminCalendar -> {
                Child.AdminCalendar(
                    adminComponent = getMainAdminComponent(childContext, true),
                    calendarComponent = CalendarComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onAdminCalendarOutput
                    )
                )
            }

            Config.MainMentoring -> {
                Child.MainMentoring(
                    mentoringComponent = getMainMentoringComponent(childContext)
                )
            }

            is Config.SecondView -> {
                println("WTFIK: ${config.isMentoring}")
                Child.SecondView(
                    mentoringComponent = getMainMentoringComponent(childContext),
                    rootComponent = RootComponentImpl(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        secondLogin = config.login,
                        secondAvatarId = config.avatarId,
                        secondFIO = config.fio,
                        firstScreen = config.config,
                        onBackButtonPress = {
                            getMainMentoringComponent(childContext).onEvent(
                                MentoringStore.Intent.SelectStudent(null)
                            ); popOnce(Child.SecondView::class)
                        },
                        isMentoring = config.isMentoring
                    ),
                    homeComponent = getMainHomeComponent(componentContext, getOld = true),
                    isMentoring = config.isMentoring
                )
            }

            Config.AdminAchievements -> {
                Child.AdminAchievements(
                    adminComponent = getMainAdminComponent(childContext, getOld = true),
                    adminAchievementsComponent = AdminAchievementsComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onAdminAchievementsOutput
                    )
                )
            }

            is Config.HomeAchievements -> {
                Child.HomeAchievements(
                    homeComponent = getMainHomeComponent(childContext, true),
                    achievementsComponent = HomeAchievementsComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onHomeAchievementsOutput,
                        login = config.studentLogin,
                        name = config.name,
                        avatarId = config.avatarId
                    )
                )
            }

            Config.AdminParents -> {
                Child.AdminParents(
                    adminComponent = getMainAdminComponent(childContext, true),
                    parentsComponent = AdminParentsComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onAdminParentsOutput
                    )
                )
            }

            is Config.HomeStudentLines -> {
                Child.HomeStudentLines(
                    homeComponent = getMainHomeComponent(childContext, true),
                    studentLinesComponent = StudentLinesComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onHomeStudentLinesOutput,
                        login = config.login
                    )
                )
            }

            is Config.QRScanner -> {
                Child.QRScanner(
                    qrComponent = QRComponent(
                        childContext,
                        storeFactory = storeFactory,
                        output = ::onQRScannerOutput,
                        isRegistration = config.isRegistration
                    )
                )
            }

            Config.MainSchool -> {
                Child.MainSchool(
                    schoolComponent = getMainSchoolComponent(
                        childContext, false
                    ),
                    ratingComponent = getMainRatingComponent(childContext, getOld = true)
                )
            }

            is Config.SchoolFormRating -> {
                Child.SchoolFormRating(
                    schoolComponent = getMainSchoolComponent(childContext, true),
                    formRatingComponent = FormRatingComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onFormRatingOutput,
                        formId = config.formId,
                        formName = config.formName,
                        formNum = config.formNum,
                        login = config.login
                    )
                )
            }

            is Config.SchoolMinistry -> {
                Child.SchoolMinistry(
                    schoolComponent = getMainSchoolComponent(childContext, true),
                    ministryComponent = MinistryComponent(
                        componentContext = childContext,
                        storeFactory = storeFactory,
                        output = ::onMinistryOutput
                    )
                )
            }
        }


    private fun onMainSchoolOutput(output: SchoolComponent.Output): Unit =
        when (output) {
            SchoolComponent.Output.NavigateBack -> popOnce(Child.MainSchool::class)

            SchoolComponent.Output.NavigateToRating -> {
                mainRatingComponent?.onEvent(RatingStore.Intent.Init)
                navigation.bringToFront(Config.MainRating)
            }

            is SchoolComponent.Output.NavigateToFormRating -> navigation.bringToFront(
                Config.SchoolFormRating(
                    login = output.login,
                    formNum = output.formNum,
                    formName = output.formName,
                    formId = output.formId
                )
            )

            is SchoolComponent.Output.NavigateToSchedule -> navigation.bringToFront(Config.AdminSchedule(output.isModer))
            SchoolComponent.Output.NavigateToMinistry -> navigation.bringToFront(Config.SchoolMinistry)
            SchoolComponent.Output.NavigateToAchievements -> navigation.bringToFront(Config.AdminAchievements)
        }

    private fun onQRScannerOutput(output: QRComponent.Output): Unit =
        when (output) {
            QRComponent.Output.Back -> popOnce(Child.QRScanner::class)
        }


    private fun onHomeAchievementsOutput(output: HomeAchievementsComponent.Output): Unit =
        when (output) {
            HomeAchievementsComponent.Output.Back -> popOnce(Child.HomeAchievements::class)
        }

    private fun onHomeStudentLinesOutput(output: StudentLinesComponent.Output): Unit =
        when (output) {
            StudentLinesComponent.Output.Back -> popOnce(Child.HomeStudentLines::class)
        }

    private fun onAdminParentsOutput(output: AdminParentsComponent.Output): Unit =
        when (output) {
            AdminParentsComponent.Output.Back -> popOnce(Child.AdminParents::class)
        }

    private fun onAdminAchievementsOutput(output: AdminAchievementsComponent.Output): Unit =
        when (output) {
            AdminAchievementsComponent.Output.Back -> popOnce(Child.AdminAchievements::class)
        }

    private fun onMainMentoringOutput(output: MentoringComponent.Output): Unit =
        when (output) {
            is MentoringComponent.Output.CreateSecondView -> navigation.bringToFront(
                Config.SecondView(
                    login = output.login,
                    fio = output.fio,
                    avatarId = output.avatarId,
                    config = output.config,
                    isMentoring = true
                )
            )

            MentoringComponent.Output.NavigateToAchievements -> navigation.bringToFront(Config.AdminAchievements)
        }

    private fun onHomeTasksOutput(output: HomeTasksComponent.Output): Unit =
        when (output) {
            HomeTasksComponent.Output.Back -> popOnce(Child.HomeTasks::class)
        }

    private fun onAdminCabinetsOutput(output: CabinetsComponent.Output): Unit =
        when (output) {
            CabinetsComponent.Output.Back -> popOnce(Child.AdminCabinets::class)
        }

    private fun onAdminCalendarOutput(output: CalendarComponent.Output): Unit =
        when (output) {
            CalendarComponent.Output.Back -> popOnce(Child.AdminCalendar::class)
        }


    private fun onAdminScheduleOutput(output: ScheduleComponent.Output): Unit =
        when (output) {
            ScheduleComponent.Output.Back -> popOnce(Child.AdminSchedule::class)
        }

    private fun onLessonReportOutput(output: LessonReportComponent.Output): Unit =
        when (output) {
            is Output.Back -> {
                if (Child.LessonReport::class.isInstance(stack.active.instance)) {
                    val component = (stack.active.instance as? Child.LessonReport)?.lessonReport!!
                    if ((component.state.value.isUpdateNeeded || component.state.value.homeTasksToEditIds.isNotEmpty() || true in component.state.value.hometasks.map { it.isNew }) && component.model.value.isEditable) {
                        component.saveQuitNameDialogComponent.onEvent(CAlertDialogStore.Intent.ShowDialog)
                    } else {
                        if (stack.value.items.size == 1 && onBackButtonPress != null) onBackButtonPress.invoke()
                        else navigation.pop {
                            (stack.active.instance as? Child.HomeAllGroupMarks)?.allGroupMarksComponent?.onEvent(
                                AllGroupMarksStore.Intent.Init)
                            mainJournalComponent?.onEvent(JournalStore.Intent.Refresh)
                        }
                    }
                } else {

                }
            }
//            {
//                mainJournalComponent?.onEvent(JournalStore.Intent.Refresh)
//                popOnce(Child.LessonReport::class)
//            }
            Output.BackAtAll -> popOnce(Child.LessonReport::class)
        }

    private fun onAllGroupMarksOutput(output: AllGroupMarksComponent.Output): Unit =
        when (output) {
            AllGroupMarksComponent.Output.Back -> popOnce(Child.HomeAllGroupMarks::class)

            is AllGroupMarksComponent.Output.OpenReport -> navigation.bringToFront(
                Config.LessonReport(
                    output.reportData
                )
            )

        }

    private fun onDetailedStupsOutput(output: DetailedStupsComponent.Output): Unit =
        when (output) {
            DetailedStupsComponent.Output.Back -> popOnce(Child.HomeDetailedStups::class)
            is DetailedStupsComponent.Output.NavigateToAchievements -> navigation.bringToFront(
                Config.HomeAchievements(
                    studentLogin = output.login,
                    name = output.name,
                    avatarId = output.avatarId
                )
            )
        }

    private fun onDnevnikRuMarksOutput(output: DnevnikRuMarksComponent.Output): Unit =
        when (output) {
            DnevnikRuMarksComponent.Output.Back -> popOnce(Child.HomeDnevnikRuMarks::class)
        }

    private fun onHomeSettingsOutput(output: SettingsComponent.Output): Unit =
        when (output) {
            SettingsComponent.Output.Back -> popOnce(Child.HomeSettings::class)
            SettingsComponent.Output.GoToZero -> navigation.replaceAll(Config.AuthActivation)
            SettingsComponent.Output.GoToScanner -> navigation.bringToFront(
                Config.QRScanner(
                    isRegistration = false
                )
            )
        }

    private fun onHomeProfileOutput(output: ProfileComponent.Output): Unit =
        when (output) {
            ProfileComponent.Output.Back -> popOnce(Child.HomeProfile::class)
            is ProfileComponent.Output.OpenAchievements -> navigation.bringToFront(
                Config.HomeAchievements(
                    studentLogin = output.login,
                    name = output.name,
                    avatarId = output.avatarId
                )
            )
        }

    private fun onAdminGroupsOutput(output: GroupsComponent.Output): Unit =
        when (output) {
            GroupsComponent.Output.Back -> popOnce(Child.AdminGroups::class)
        }

    private fun onAdminUsersOutput(output: UsersComponent.Output): Unit =
        when (output) {
            UsersComponent.Output.Back -> popOnce(Child.AdminUsers::class)
        }

    private fun onFormRatingOutput(output: FormRatingComponent.Output): Unit =
        when (output) {
            FormRatingComponent.Output.Back -> popOnce(Child.SchoolFormRating::class)
            is FormRatingComponent.Output.NavigateToProfile -> navigation.bringToFront(
                Config.HomeProfile(
                    studentLogin = output.studentLogin,
                    fio = output.fio,
                    avatarId = output.avatarId,
                    isOwner = false,
                    isCanEdit = false
                )
            )
        }

    private fun onMinistryOutput(output: MinistryComponent.Output): Unit =
        when (output) {
            MinistryComponent.Output.Back -> popOnce(Child.SchoolMinistry::class)
        }

    private fun onRatingOutput(output: RatingComponent.Output): Unit =
        when (output) {
            RatingComponent.Output.NavigateToSettings -> navigation.bringToFront(Config.HomeSettings)
            is RatingComponent.Output.NavigateToProfile -> navigation.bringToFront(
                Config.HomeProfile(
                    studentLogin = output.studentLogin,
                    fio = output.fio,
                    avatarId = output.avatarId,
                    isOwner = false,
                    isCanEdit = false
                )
            )

            RatingComponent.Output.Back -> popOnce(Child.MainRating::class)
        }

    private fun onJournalOutput(output: JournalComponent.Output): Unit =
        when (output) {
            is JournalComponent.Output.NavigateToLessonReport -> navigation.bringToFront(
                Config.LessonReport(
                    output.reportData
                )
            )


            JournalComponent.Output.NavigateToSettings -> navigation.bringToFront(Config.HomeSettings)
        }

    private fun onAdminOutput(output: AdminComponent.Output): Unit =
        when (output) {
            AdminComponent.Output.NavigateToUsers ->
                navigation.pushToFront(Config.AdminUsers)

            AdminComponent.Output.NavigateToGroups ->
                navigation.bringToFront(Config.AdminGroups)

            AdminComponent.Output.NavigateToCabinets ->
                navigation.bringToFront(Config.AdminCabinets)

            AdminComponent.Output.NavigateToCalendar ->
                navigation.bringToFront(Config.AdminCalendar)

            AdminComponent.Output.NavigateToAchievements -> navigation.bringToFront(Config.AdminAchievements)
            AdminComponent.Output.NavigateToParents -> navigation.bringToFront(Config.AdminParents)
        }

//    private fun onAdminMentorsOutput(output: MentorsComponent.Output): Unit =
//        when (output) {
//            else -> {}
//        }

    private fun onLoginOutput(output: LoginComponent.Output): Unit =
        when (output) {
            LoginComponent.Output.BackToActivation -> navigation.pop()
            LoginComponent.Output.NavigateToMain -> navigateAfterAuth()
        }

    private fun onActivationOutput(output: ActivationComponent.Output): Unit =
        when (output) {
            is ActivationComponent.Output.NavigateToLogin -> navigation.bringToFront(Config.AuthLogin(login = output.login))
            ActivationComponent.Output.NavigateToMain -> navigateAfterAuth()
            ActivationComponent.Output.GoToScanner -> navigation.bringToFront(
                Config.QRScanner(
                    isRegistration = true
                )
            )
        }

    private fun onHomeOutput(output: HomeComponent.Output): Unit =
        when (output) {
            HomeComponent.Output.NavigateToSettings -> navigation.bringToFront(Config.HomeSettings)

            is HomeComponent.Output.NavigateToDnevnikRuMarks -> navigation.bringToFront(
                Config.HomeDnevnikRuMarks(
                    output.studentLogin
                )
            )

            is HomeComponent.Output.NavigateToDetailedStups -> navigation.bringToFront(
                Config.HomeDetailedStups(
                    studentLogin = output.studentLogin,
                    reason = output.reason.toString(),
                    name = output.name,
                    avatarId = output.avatarId
                )
            )

            is HomeComponent.Output.NavigateToAllGroupMarks -> navigation.bringToFront(
                Config.HomeAllGroupMarks(
                    groupId = output.groupId,
                    groupName = output.groupName,
                    subjectName = output.subjectName,
                    subjectId = output.subjectId,
                    teacherLogin = output.teacherLogin
                )
            )

            is HomeComponent.Output.NavigateToProfile -> navigation.bringToFront(
                Config.HomeProfile(
                    studentLogin = output.studentLogin,
                    fio = output.fio,
                    avatarId = output.avatarId,
                    isOwner = true,
                    isCanEdit = secondLogin == null
                )
            )

            is HomeComponent.Output.NavigateToTasks ->
                navigation.bringToFront(
                    Config.HomeTasks(
                        studentLogin = output.studentLogin,
                        avatarId = output.avatarId,
                        name = output.name
                    )
                )

            is HomeComponent.Output.NavigateToStudentLines -> navigation.bringToFront(
                Config.HomeStudentLines(
                    login = output.studentLogin
                )
            )

            is HomeComponent.Output.NavigateToChildren -> navigation.bringToFront(
                Config.SecondView(
                    login = output.studentLogin,
                    fio = output.fio,
                    avatarId = output.avatarId,
                    config = RootComponent.Config.MainHome,
                    isMentoring = false
                )
            )

            is HomeComponent.Output.NavigateToSchool -> navigation.bringToFront(
                Config.MainSchool
            )
        }

    override fun onEvent(event: RootStore.Intent) {
        rootStore.accept(event)
    }

    override fun onOutput(output: RootComponent.Output): Unit =
        when (output) {

            RootComponent.Output.NavigateToHome -> {
                navigation.bringToFront(
                    Config.MainHome
                )
            }

            RootComponent.Output.NavigateToJournal -> navigation.bringToFront(Config.MainJournal)

            RootComponent.Output.NavigateToAdmin -> navigation.bringToFront(Config.MainAdmin)

            RootComponent.Output.NavigateToSchedule -> navigation.bringToFront(Config.AdminSchedule(isModerator = true))

            RootComponent.Output.NavigateToSchool -> navigation.bringToFront(Config.MainSchool)

            RootComponent.Output.NavigateToAuth ->
                navigation.replaceAll(Config.AuthActivation)

            RootComponent.Output.NavigateToMentoring -> navigation.bringToFront(Config.MainMentoring)
        }


    private fun navigateAfterAuth() {
        authRepository = Inject.instance()
        componentContext = childContext(authRepository.fetchLogin())
        rootStore.accept(
            RootStore.Intent.UpdatePermissions(
                role = authRepository.fetchRole(),
                moderation = authRepository.fetchModeration(),
                birthday = authRepository.fetchBirthday(),
                version = applicationVersion
            )
        )

        getMainJournalComponent(componentContext, false)
        getMainMentoringComponent(componentContext, false)
        getMainRatingComponent(componentContext, false)
        getMainHomeComponent(componentContext, false)
        getMainAdminComponent(componentContext, false)
        navigation.replaceAll(
            Config.MainHome
        )
    }

    sealed interface DeepLink {
        data object None : DeepLink
        class Web(val path: String) : DeepLink
    }


    init {
        backHandler.register(backCallback)
//        authRepository.deleteToken()
        webHistoryController?.attach(
            navigator = navigation,
            stack = stack,
            getPath = ::getPathForConfig,
            getConfiguration = ::getConfigForPath,
            serializer = Config.serializer()
        )
//        backHandler.register(backCallback)
//        updateBackCallback(false)
        if (secondLogin == null) {
            if (authRepository.isUserLoggedIn()) {
                rootStore.accept(RootStore.Intent.CheckConnection)
                rootStore.accept(RootStore.Intent.HideGreetings())
            }
        } else {

        }
    }

    private fun popOnce(child: KClass<out Child>) {
        if (child.isInstance(stack.active.instance)) {
            if (stack.value.items.size == 1 && onBackButtonPress != null) onBackButtonPress.invoke()
            else navigation.pop()
        }
    }

    private fun getInitialStack(webHistoryPaths: List<String>?, deepLink: DeepLink): List<Config> =
        webHistoryPaths
            ?.takeUnless(List<*>::isEmpty)
            ?.map(::getConfigForPath)
            ?: getInitialStack(deepLink)

    private fun getInitialStack(deepLink: DeepLink): List<Config> = //listOf(getFirstScreen())
        when (deepLink) {
            is DeepLink.None -> {
//                val fs = getFirstScreen()
//                val list = mutableListOf<Config>()
//                if (fs !in listOf(Config.AuthActivation, Config.MainHome)) {
//                    list.add(Config.MainHome)
//                }
//                list.add(fs)
//                list
                listOf(getFirstScreen())
            }

            is DeepLink.Web -> listOf(getConfigForPath(deepLink.path))
        }

    private fun getPathForConfig(config: Config): String =
        when (config) {
//            Config.AuthLogin -> "/$WEB_PATH_AUTH_LOGIN"
//            Config.AuthActivation -> {
//                println("gogo"); "/$WEB_PATH_AUTH_ACTIVATION"
//            }
//
//            Config.MainHome -> "/$WEB_PATH_MAIN_HOME"
//            Config.MainJournal -> "/$WEB_PATH_MAIN_JOURNAL"
//            Config.MainAdmin -> "/$WEB_PATH_MAIN_ADMIN"
//
////            Config.AdminMentors -> "/$WEB_PATH_ADMIN_MENTORS"
//            Config.AdminUsers -> "/$WEB_PATH_ADMIN_USERS"
//            Config.AdminGroups -> "/$WEB_PATH_ADMIN_GROUPS"
////            Config.AdminStudents -> "/$WEB_PATH_ADMIN_STUDENTS"
//            is Config.LessonReport -> "/$WEB_PATH_JOURNAL_LESSON_REPORT/${config.reportData.header.reportId}"
//            Config.HomeSettings -> "/$WEB_PATH_HOME_SETTINGS"
//            is Config.HomeDnevnikRuMarks -> "/$WEB_PATH_HOME_SETTINGS/${config.studentLogin}"
//            is Config.HomeDetailedStups -> "/$WEB_PATH_HOME_DETAILED_STUPS/${config.studentLogin}/${config.reason}"
//            is Config.HomeAllGroupMarks -> "/$WEB_PATH_HOME_DETAILED_STUPS/${config.subjectId}/${config.groupId}"
//            //else -> "/"
//            Config.AdminCabinets -> "/$WEB_PATH_ADMIN_CABINETS"
//            Config.AdminCalendar -> "/$WEB_PATH_ADMIN_CALENDAR"
//            Config.AdminSchedule -> "/$WEB_PATH_ADMIN_SCHEDULE"
//            is Config.HomeProfile -> "/$WEB_PATH_HOME_PROFILE/${config.studentLogin}"
//            is Config.HomeTasks -> "/$WEB_PATH_HOME_TASKS"
//            Config.MainRating -> "/$WEB_PATH_MAIN_RATING"
//            Config.MainMentoring -> "/TODO"
//            is Config.SecondView -> "/TODO"
            else -> {
                "/"
            }
        }

    //
    private fun getConfigForPath(path: String): Config {
        return when (path.removePrefix("/")) {
//            WEB_PATH_AUTH_LOGIN -> Config.AuthLogin
//            WEB_PATH_AUTH_ACTIVATION -> Config.AuthActivation
//
//
//            WEB_PATH_MAIN_HOME -> Config.MainHome
//            WEB_PATH_MAIN_JOURNAL -> Config.MainJournal
//            WEB_PATH_MAIN_ADMIN -> Config.MainAdmin
//
////            WEB_PATH_ADMIN_MENTORS -> Config.AdminMentors
//            WEB_PATH_ADMIN_USERS -> Config.AdminUsers
//            WEB_PATH_ADMIN_GROUPS -> Config.AdminGroups
//            WEB_PATH_HOME_DNEVNIK_RU_MARKS.split("/")[0] -> Config.HomeDnevnikRuMarks(
//                studentLogin = path.split("/").last()
//            )
//
//            WEB_PATH_HOME_DETAILED_STUPS.split("/")[0] -> Config.HomeDetailedStups(
//                studentLogin = path.split("/").last(),
//                reason = path.split("/").last()
//            )
//
//            WEB_PATH_HOME_ALL_GROUP_MARKS.split("/")[0] -> Config.HomeAllGroupMarks(
//                groupName = "",
//                subjectId = 0,
//                subjectName = "",
//                groupId = 0
//            )
//
////            WEB_PATH_ADMIN_STUDENTS -> Config.AdminStudents
//            WEB_PATH_JOURNAL_LESSON_REPORT.split("/")[0] -> Config.LessonReport(
//                ReportData(
//                    ReportHeader(
//                        reportId = path.removePrefix("/").split("/")[1].toInt(),
//                        subjectName = "",
//                        subjectId = 0,
//                        groupName = "",
//                        groupId = 0,
//                        teacherName = "",
//                        teacherLogin = "",
//                        date = "",
//                        time = "",
//                        status = false,
//                        theme = "",
//                        module = "1"
//                    ),
//                    description = "",
//                    ids = 0,
//                    isMentorWas = false,
//                    editTime = "",
//                    isEditable = false,
//                    customColumns = emptyList()
//                )
//            )
//
//            WEB_PATH_HOME_SETTINGS -> Config.HomeSettings
            else -> Config.AuthActivation
        }
    }

}