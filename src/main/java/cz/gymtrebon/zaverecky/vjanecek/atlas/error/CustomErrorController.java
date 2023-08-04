package cz.gymtrebon.zaverecky.vjanecek.atlas.error;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/error404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errors/error403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/error500";
            }else{
                model.addAttribute("code", Integer.toString(statusCode));
                return "errors/undefined_error";
            }

            /*else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // Access Denied error
                return "error/access-denied";
            }*/
        }
        log.error("status is null");
        return "errors/undefined_error";
    }

    @RequestMapping(value = ERROR_PATH+"/403")
    public String accessDenied403() {
        return "errors/error403";
    }
}