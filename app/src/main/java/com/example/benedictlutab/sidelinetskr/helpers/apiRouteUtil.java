package com.example.benedictlutab.sidelinetskr.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
//      public String DOMAIN = "http://192.168.0.101/Sideline/";
//    public String DOMAIN = "http://192.168.43.218/Sideline//admin_area/";
//    public String DOMAIN = "http://192.168.0.101/Sideline/admin_area/";
//    public String DOMAIN = "http://192.168.0.36/Sideline/";
    public String DOMAIN = "http://192.168.1.9/Sideline/admin_area/";

    // Routes
    public String URL_LOGIN                 = DOMAIN + "api/common/login.php";
    public String URL_CHECK_CONNECTION      = DOMAIN + "api/common/checkConnection.php";
    public String URL_TASK_DETAILS          = DOMAIN + "api/common/fetchTaskDetails.php";
    public String URL_PROFILE_DETAILS       = DOMAIN + "api/common/loadUserInformation.php";
    public String URL_SEND_EVAL             = DOMAIN + "api/common/sendEvaluation.php";
    public String URL_LOAD_EVAL             = DOMAIN + "api/common/loadEvaluationList.php";
    public String URL_CHANGE_PASS           = DOMAIN + "api/common/changePassword.php";
    public String URL_FETCH_TASK_HISTORY    = DOMAIN + "api/common/fetchTaskHistory.php";

    public String URL_MY_SKILLS             = DOMAIN + "api/tasker/mySkills.php";
    public String URL_AVAILABLE_TASKS       = DOMAIN + "api/tasker/availableTasks.php";
    public String URL_SEND_OFFER            = DOMAIN + "api/tasker/sendOffer.php";
    public String URL_TASK_SCHEDULE         = DOMAIN + "api/tasker/taskSchedule.php";
    public String URL_FETCH_BALANCE         = DOMAIN + "api/tasker/fetchBalance.php";
    public String URL_UPDATE_WALLET         = DOMAIN + "api/tasker/updateWalletBalance.php";
    public String URL_FETCH_LOAD_HISTORY    = DOMAIN + "api/tasker/fetchLoadHistory.php";
    public String URL_START_TASK            = DOMAIN + "api/tasker/startTask.php";
    public String URL_VERIFY_CODE           = DOMAIN + "api/tasker/verifyTransCode.php";
    public String URL_LOAD_TRANS_SUMMARY    = DOMAIN + "api/tasker/loadTransSummary.php";
    public String URL_COLLECT_PAYMENT       = DOMAIN + "api/tasker/collectPayment.php";
    public String URL_EVAL_DETAILS          = DOMAIN + "api/tasker/fetchEvalDetails.php";
    public String URL_GET_OFFER_COUNT       = DOMAIN + "api/tasker/getNumOffers.php";
    public String URL_UPDATE_SHORT_BIO      = DOMAIN + "api/tasker/updateAboutMe.php";

    public String URL_GET_TOKEN             = DOMAIN + "api/braintree/main.php";
    public String URL_CHECKOUT              = DOMAIN + "api/braintree/checkout.php";

    public String URL_FOREX                 = "http://apilayer.net/api/live?access_key=1a4f1cd836638ee571372da362eec93b&currencies=PHP";
}

