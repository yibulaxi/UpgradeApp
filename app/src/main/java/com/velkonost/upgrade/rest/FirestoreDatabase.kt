package com.velkonost.upgrade.rest

interface TableFields {
    val fieldName: String
}

enum class UserSettingsFields(override val fieldName: String) : TableFields {
    Id("id"),
    UserId("userId"),
    AuthType("authType"),
    Login("login"),
    Password("password"),
    Difficulty("difficulty"),
    IsPushAvailable("isPushAvailable"),
    Greeting("greeting"),
    DateRegistration("dateRegistration"),
    DateLastLogin("dateLastLogin"),
    Avatar("avatar"),
    Locale("locale")
}

enum class UserInterestsFields(override val fieldName: String) : TableFields {
    Id("id"),
    Name("name"),
    Description("description"),
    StartValue("startValue"),
    CurrentValue("currentValue"),
    DateLastUpdate("dateLastUpdate"),
    Icon("icon"),
    Order("order")
}

enum class UserDiaryFields(override val fieldName: String) : TableFields {
    Id("id"),
    DiaryNoteId("diaryNoteId"),
    NoteType("noteType"),
    Date("date"),
    Title("title"),
    Text("text"),
    Media("media"),
    ChangeOfPoints("changeOfPoints"),
    DatetimeStart("datetimeStart"),
    DatetimeEnd("datetimeEnd"),
    IsActiveNow("isActiveNow"),
    Interest("interest"),
    InterestId("interestId"),
    InterestName("interestName"),
    InterestIcon("interestIcon"),
    InitialAmount("initialAmount"),
    Regularity("regularity"),
    IsPushAvailable("isPushAvailable"),
    Color("color"),
    CurrentAmount("currentAmount"),
    DatesCompletion("datesCompletion"),
    DatesCompletionDatetime("datesCompletionDatetime"),
    DatesCompletionIsCompleted("datesCompletionIsCompleted"),
    Tags("tags")
}

interface FirestoreDatabaseTable {
    val tableName: String
    val tableFields: HashMap<TableFields, String>
}

class UserSettingsTable : FirestoreDatabaseTable {
    override val tableName: String = "user_settings"

    override val tableFields: HashMap<TableFields, String> =
        hashMapOf(
            UserSettingsFields.Id to UserSettingsFields.Id.fieldName,
            UserSettingsFields.AuthType to UserSettingsFields.AuthType.fieldName,
            UserSettingsFields.Login to UserSettingsFields.Login.fieldName,
            UserSettingsFields.Password to UserSettingsFields.Password.fieldName,
            UserSettingsFields.Difficulty to UserSettingsFields.Difficulty.fieldName,
            UserSettingsFields.IsPushAvailable to UserSettingsFields.IsPushAvailable.fieldName,
            UserSettingsFields.Greeting to UserSettingsFields.Greeting.fieldName,
            UserSettingsFields.DateRegistration to UserSettingsFields.DateRegistration.fieldName,
            UserSettingsFields.DateLastLogin to UserSettingsFields.DateLastLogin.fieldName,
            UserSettingsFields.Avatar to UserSettingsFields.Avatar.fieldName,
            UserSettingsFields.Locale to UserSettingsFields.Locale.fieldName
        )
}

class UserInterestsTable : FirestoreDatabaseTable {
    override val tableName: String = "user_interests"
    override val tableFields: HashMap<TableFields, String> =
        hashMapOf(
            UserInterestsFields.Id to UserInterestsFields.Id.fieldName,
            UserInterestsFields.Name to UserInterestsFields.Name.fieldName,
            UserInterestsFields.Description to UserInterestsFields.Description.fieldName,
            UserInterestsFields.StartValue to UserInterestsFields.StartValue.fieldName,
            UserInterestsFields.CurrentValue to UserInterestsFields.CurrentValue.fieldName,
            UserInterestsFields.DateLastUpdate to UserInterestsFields.DateLastUpdate.fieldName,
            UserInterestsFields.Icon to UserInterestsFields.Icon.fieldName,
            UserInterestsFields.Order to UserInterestsFields.Order.fieldName
        )
}

class UserDiaryTable : FirestoreDatabaseTable {
    override val tableName: String = "user_diary"
    override val tableFields: HashMap<TableFields, String> =
        hashMapOf(
            UserDiaryFields.Id to UserDiaryFields.Id.fieldName,
            UserDiaryFields.DiaryNoteId to UserDiaryFields.DiaryNoteId.fieldName,
            UserDiaryFields.NoteType to UserDiaryFields.NoteType.fieldName,
            UserDiaryFields.Date to UserDiaryFields.Date.fieldName,
            UserDiaryFields.Title to UserDiaryFields.Title.fieldName,
            UserDiaryFields.Text to UserDiaryFields.Text.fieldName,
            UserDiaryFields.Media to UserDiaryFields.Media.fieldName,
            UserDiaryFields.ChangeOfPoints to UserDiaryFields.ChangeOfPoints.fieldName,
            UserDiaryFields.DatetimeStart to UserDiaryFields.DatetimeStart.fieldName,
            UserDiaryFields.DatetimeEnd to UserDiaryFields.DatetimeEnd.fieldName,
            UserDiaryFields.IsActiveNow to UserDiaryFields.IsActiveNow.fieldName,
            UserDiaryFields.Interest to UserDiaryFields.Interest.fieldName,
            UserDiaryFields.InterestId to UserDiaryFields.InterestId.fieldName,
            UserDiaryFields.InterestName to UserDiaryFields.InterestName.fieldName,
            UserDiaryFields.InterestIcon to UserDiaryFields.InterestIcon.fieldName,
            UserDiaryFields.InitialAmount to UserDiaryFields.InitialAmount.fieldName,
            UserDiaryFields.Regularity to UserDiaryFields.Regularity.fieldName,
            UserDiaryFields.IsPushAvailable to UserDiaryFields.IsPushAvailable.fieldName,
            UserDiaryFields.Color to UserDiaryFields.Color.fieldName,
            UserDiaryFields.CurrentAmount to UserDiaryFields.CurrentAmount.fieldName,
            UserDiaryFields.DatesCompletion to UserDiaryFields.DatesCompletion.fieldName,
            UserDiaryFields.DatesCompletionDatetime to UserDiaryFields.DatesCompletionDatetime.fieldName,
            UserDiaryFields.DatesCompletionIsCompleted to UserDiaryFields.DatesCompletionIsCompleted.fieldName,
            UserDiaryFields.Tags to UserDiaryFields.Tags.fieldName,
        )
}
