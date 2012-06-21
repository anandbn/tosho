/*
 * Copyright, 1999-2012, salesforce.com All Rights Reserved Company Confidential
 */
package platform.connect.api;

import java.util.Locale;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Describes contextual information about the current user.
 * 
 * @author spepper
 * @since 180
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CanvasUserContext{

    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String fullName;
    private Locale locale;
    private Locale language;
    private String timeZone;
    private String profileId;
    private String roleId;
    private String userType;
    private String currencyISOCode;
    private boolean accessibilityMode;
    private String profilePhotoUrl;
    private String profileThumbnailUrl;

    /**
     * The Salesforce user identifier.
     */
    @JsonProperty("userId")
    public String getUserId(){
        return this.userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    /**
     * The Salesforce username.
     */
    @JsonProperty("userName")
    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String username){
        this.userName = username;
    }

    /**
     * User's first name.
     */
    @JsonProperty("firstName")
    public String getFirstName(){
        return this.firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    /**
     * User's last name.
     */
    @JsonProperty("lastName")
    public String getLastName(){
        return this.lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    /**
     * Indicates whether user interface modifications for the visually impaired are on (true) or off (false).
     */
    @JsonProperty("accessibilityModeEnabled")
    public boolean isAccessibilityMode(){
        return this.accessibilityMode;
    }

    public void setAccessibilityMode(boolean accessibilityMode){
        this.accessibilityMode = accessibilityMode;
    }

    /**
     * The user's email address.
     */
    @JsonProperty("email")
    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    /**
     * User's full name.
     */
    @JsonProperty("fullName")
    public String getFullName(){
        return this.fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    /**
     * User\u2019s locale, which controls the formatting of dates and choice of symbols for currency.
     */
    @JsonProperty("locale")
    public Locale getLocale(){
        return this.locale;
    }

    public void setLocale(Locale locale){
        this.locale = locale;
    }

    /**
     * User's language, which controls the language for labels displayed in an application.
     */
    @JsonProperty("language")
    public Locale getLanguage(){
        return this.language;
    }

    public void setLanguage(Locale language){
        this.language = language;
    }

    /**
     * The user's configured timezone.
     */
    @JsonProperty("timeZone")
    public String getTimeZone(){
        return this.timeZone;
    }

    public void setTimeZone(String timezone){
        this.timeZone = timezone;
    }

    /**
     * Information about the user's profile identifier.
     */
    @JsonProperty("profileId")
    public String getProfileId(){
        return this.profileId;
    }

    public void setProfileId(String profileId){
        this.profileId = profileId;
    }

    /**
     * Role ID of the role currently assigned to the user.
     */
    @JsonProperty("roleId")
    public String getRoleId(){
        return this.roleId;
    }

    public void setRoleId(String roleId){
        this.roleId = roleId;
    }

    /**
     * Current user's license type in label form.
     */
    public String getUserType(){
        return this.userType;
    }

    public void setUserType(String userType){
        this.userType = userType;
    }

    /**
     * Current user's default currency ISO code (applies only if multi-currency is enabled for the org).
     */
    public String getCurrencyISOCode(){
        return this.currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode){
        this.currencyISOCode = currencyISOCode;
    }

    /**
     * Returns the full profile photo of the current user.
     */
    public String getProfilePhotoUrl(){
        return this.profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl){
        this.profilePhotoUrl = profilePhotoUrl;
    }

    /**
     * Returns the thumbnail photo of the current user.
     */
    public String getProfileThumbnailUrl(){
        return this.profileThumbnailUrl;
    }

    public void setProfileThumbnailUrl(String profileThumbnailUrl){
        this.profileThumbnailUrl = profileThumbnailUrl;
    }

    @Override
    public String toString()
    {
        return userId+ ","+
               userName+ ","+
               firstName+","+
               lastName+ ","+
               email+ ","+
               fullName+ ","+
               locale+ ","+
               language+ ","+
               timeZone+ ","+
               profileId+ ","+
               roleId+ ","+
               userType+ ","+
               currencyISOCode+ ","+
               accessibilityMode+ ","+
               profilePhotoUrl+","+
               profileThumbnailUrl;

    }
}
