package org.Ermas.book.chap13;

import javax.servlet.http.HttpSession;

import org.Ermas.book.chap11.Member;
import org.Ermas.book.chap11.MemberDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {


	@Autowired
	MemberDao memberDao;

	static final Logger logger = LogManager.getLogger();

	@GetMapping("/loginForm")
	public String form() {
		return "login/loginForm";
	}

	@PostMapping("/login")
	public String submit(@RequestParam("email") String email,
			@RequestParam("password") String password, 
			@RequestParam("returnUrl") String returnUrl, HttpSession session) {
		try {
			Member member = memberDao.selectByLogin(email, password);
			session.setAttribute("MEMBER", member);
			logger.debug("로그인 성공. {}", member);
			return "redirect:" + returnUrl;
		} catch (EmptyResultDataAccessException e) {
			logger.debug("로그인 실패. email={}", email);
			return "redirect:/app/loginForm?mode=FAILURE&email=" + email
					+ "&returnUrl=" + returnUrl;
		}
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
}
}
