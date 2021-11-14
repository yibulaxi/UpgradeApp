package com.velkonost.upgrade.rest

interface TableFields {
    val fieldName: String
}

enum class UserSettingsFields(override val fieldName: String) : TableFields {
    AuthType("auth_type"),
    Login("login"),
    Password("password"),
    Difficulty("difficulty"),
    IsPushAvailable("is_push_available"),
    Greeting("greeting"),
    DateRegistration("date_registration"),
    DateLastLogin("date_last_login"),
    Avatar("avatar"),
    Locale("locale")
}

enum class UserInterestsFields(override val fieldName: String) : TableFields {
    Id("id"),
    Name("name"),
    Description("description"),
    StartValue("start_value"),
    CurrentValue("current_value"),
    DateLastUpdate("date_last_update"),
    Icon("icon"),
    Order("order")
}

enum class UserDiaryFields(override val fieldName: String) : TableFields {
    Id("id"),
    NoteType("note_type"),
    Date("date"),
    Title("title"),
    Text("text"),
    Media("media"),
    IsPositive("is_positive"),
    DatetimeStart("datetime_start"),
    DatetimeEnd("datetime_end"),
    IsActiveNow("is_active_now"),
    Interest("interest"),
    InterestId("interest_id"),
    InterestName("interest_name"),
    InterestIcon("interest_icon"),
    InitialAmount("initial_amount"),
    Regularity("regularity"),
    IsPushAvailable("is_push_available"),
    Color("color"),
    CurrentAmount("current_amount"),
    DatesCompletion("dates_completion"),
    DatesCompletionDatetime("dates_completion_datetime"),
    DatesCompletionIsCompleted("dates_completion_is_completed"),
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
            UserDiaryFields.NoteType to UserDiaryFields.NoteType.fieldName,
            UserDiaryFields.Date to UserDiaryFields.Date.fieldName,
            UserDiaryFields.Title to UserDiaryFields.Title.fieldName,
            UserDiaryFields.Text to UserDiaryFields.Text.fieldName,
            UserDiaryFields.Media to UserDiaryFields.Media.fieldName,
            UserDiaryFields.IsPositive to UserDiaryFields.IsPositive.fieldName,
            UserDiaryFields.DatetimeStart to UserDiaryFields.DatetimeStart.fieldName,
            UserDiaryFields.DatetimeEnd to UserDiaryFields.DatetimeEnd.fieldName,
            UserDiaryFields.IsActiveNow to UserDiaryFields.IsActiveNow.fieldName,
            UserDiaryFields.Interest to UserDiaryFields.Interest.fieldName,
            UserDiaryFields.InterestId to UserDiaryFields.InterestId.fieldName,
            UserDiaryFields.InterestName to UserDiaryFields.InterestName.fieldName,
            UserDiaryFields.InterestIcon to UserDiaryFields.InterestIcon.fieldName,
            UserDiaryFields.InitialAmount to UserDiaryFields.InitialAmount.fieldName,
            UserDiaryFields.Regularity to UserDiaryFields.Regularity.fieldName,
            UserSettingsFields.IsPushAvailable to UserSettingsFields.IsPushAvailable.fieldName,
            UserDiaryFields.Color to UserDiaryFields.Color.fieldName,
            UserDiaryFields.CurrentAmount to UserDiaryFields.CurrentAmount.fieldName,
            UserDiaryFields.DatesCompletion to UserDiaryFields.DatesCompletion.fieldName,
            UserDiaryFields.DatesCompletionDatetime to UserDiaryFields.DatesCompletionDatetime.fieldName,
            UserDiaryFields.DatesCompletionIsCompleted to UserDiaryFields.DatesCompletionIsCompleted.fieldName,
            UserDiaryFields.Tags to UserDiaryFields.Tags.fieldName,
            )
}
