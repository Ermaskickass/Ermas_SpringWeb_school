package org.Ermas.article;

import java.util.List;

import org.Ermas.book.chap11.Member;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class ArticleController {

	@Autowired
	ArticleDao articleDao;

	static final Logger logger = LogManager.getLogger();

	@GetMapping("/article/list")
	public void articleList(
			@RequestParam(value = "page", defaultValue = "1") int page,
			Model model) {

		final int COUNT = 100;
		int offset = (page - 1) * COUNT;

		List<Article> articleList = articleDao.listArticles(offset, COUNT);
		int totalCount = articleDao.getArticlesCount();
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("articleList", articleList);
	}

	@GetMapping("/article/view")
	public void articleView(@RequestParam("articleId") String articleId,
			Model model) {
		Article article = articleDao.getArticle(articleId);
		model.addAttribute("article", article);
	}

	@PostMapping("/article/s/add")
	public String articleAdd(Article article,
			@SessionAttribute("MEMBER") Member member) {
		article.setUserId(member.getMemberId());
		article.setName(member.getName());
		articleDao.addArticle(article);
		return "redirect:/app/article/list";
	}

	@GetMapping("/article/s/edit")
	public void updateForm(@RequestParam("articleId") String articleId,
			@SessionAttribute("MEMBER") Member member, Model model) {
		Article article = articleDao.getArticle(articleId);

		if (!member.getMemberId().equals(article.getUserId()))
			throw new RuntimeException("No Authority!");

		model.addAttribute("article", article);
	}

	@PostMapping("/article/s/update")
	public String update(Article article,
			@SessionAttribute("MEMBER") Member member) {
		article.setUserId(member.getMemberId());
		int updatedRows = articleDao.updateArticle(article);

		if (updatedRows == 0)
			throw new RuntimeException("No Authority!");

		return "redirect:/app/article/view?articleId=" + article.getArticleId();
	}

	
	@GetMapping("/article/s/delete")
	public String delete(@RequestParam("articleId") String articleId,
			@SessionAttribute("MEMBER") Member member) {
		int updatedRows = articleDao.deleteArticle(articleId,
				member.getMemberId());

		if (updatedRows == 0)
			throw new RuntimeException("No Authority!");

		logger.debug("글을 삭제했습니다. articleId={}", articleId);
		return "redirect:/app/article/list";
	}
}


