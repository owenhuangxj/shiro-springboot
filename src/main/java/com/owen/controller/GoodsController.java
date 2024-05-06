package com.owen.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2024/5/6
 * 创建人:Owen
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
	@GetMapping("/delete")
	public String delete() {
		return "Deleted!!!";
	}
}
