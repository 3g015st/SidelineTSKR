package com.example.benedictlutab.sidelinetskr.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
//    public String DOMAIN = "http://192.168.1.5/Sideline/";
      public String DOMAIN = "http://192.168.1.7/Sideline/";
//    public String DOMAIN = "http://192.168.43.218/Sideline/";

    // Routes
    public String URL_LOGIN                 = DOMAIN + "api/common/login.php";
    public String URL_CHECK_CONNECTION      = DOMAIN + "api/common/checkConnection.php";

    public String URL_MY_SKILLS             = DOMAIN + "api/tasker/mySkills.php";
    public String URL_AVAILABLE_TASKS       = DOMAIN + "api/tasker/availableTasks.php";
    public String URL_TASK_DETAILS          = DOMAIN + "api/tasker/fetchTaskDetails.php";
    public String URL_SEND_OFFER            = DOMAIN + "api/tasker/sendOffer.php";
}
