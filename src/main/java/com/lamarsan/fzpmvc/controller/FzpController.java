package com.lamarsan.fzpmvc.controller;

import com.lamarsan.fzpmvc.annaotation.LamarAutowired;
import com.lamarsan.fzpmvc.annaotation.LamarRequestMapping;
import com.lamarsan.fzpmvc.annaotation.LamarController;
import com.lamarsan.fzpmvc.annaotation.LamarRequestParam;
import com.lamarsan.fzpmvc.service.FzpService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * className: LamarController
 * description: TODO
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 19:42
 */

@LamarController
@LamarRequestMapping("/fzp")
public class FzpController {
    /**
     * map.get(key)
     */
    @LamarAutowired("FzpServiceImpl")
    private FzpService fzpService;

    @LamarRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @LamarRequestParam("name") String name,
                      @LamarRequestParam("age") String age) throws IOException {
        PrintWriter pw = response.getWriter();
        String result = fzpService.query(name, age);
        pw.write(result);
    }
}
