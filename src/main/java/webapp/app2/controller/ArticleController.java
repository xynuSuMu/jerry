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

    //阅读模式
    @JerryRequestMapping(value = "/doc", method = RequestMethod.GET)
    public String getDoc() {
        return "doc.html";
    }

    @JerryRequestMapping(value = "/doc/directory", method = RequestMethod.GET)
    public String getDirectory() {
        return "directory.html";
    }

    @JerryRequestMapping(value = "/doc/content", method = RequestMethod.GET)
    public String getContent() {
        return "markdown.html";
    }
}
