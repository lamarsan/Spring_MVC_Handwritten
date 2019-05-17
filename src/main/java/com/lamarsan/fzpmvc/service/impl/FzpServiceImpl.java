package com.lamarsan.fzpmvc.service.impl;

import com.lamarsan.fzpmvc.annaotation.LamarService;
import com.lamarsan.fzpmvc.service.FzpService;

/**
 * className: FzpServiceImpl
 * description: map.put("FzpServiceImpl",new FzpServiceImpl())
 *
 * @author hasee
 * @version 1.0
 * @date 2019/5/16 19:43
 */
@LamarService("FzpServiceImpl")
public class FzpServiceImpl implements FzpService {
    public String query(String name, String age) {
        return "name==="+name+"\nage==="+age;
    }
}
