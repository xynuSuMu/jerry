package webapp.app2.controller;

import annotation.JerryController;
import annotation.JerryRequestMapping;
import annotation.RequestMethod;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-19 16:47
 */
@JerryController
@JerryRequestMapping("")
public class ArticleController {

    @JerryRequestMapping(value = "/doc", method = RequestMethod.GET)
    public String getDoc() {
        return "doc.html";
    }
}
