package com.websocket.wstutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@SpringBootApplication
public class WsTutorialApplication {

	private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(WsTutorialApplication.class, args);
	}

	@GetMapping("/questions")
	public SseEmitter questions() {
		SseEmitter sseEmitter = new SseEmitter();

		emitters.add(sseEmitter);

		sseEmitter.onCompletion(() -> {
			System.out.println("- complete emiiter -");
			emitters.remove(sseEmitter);
		});

		return sseEmitter;
	}

	@PostMapping("/new-question")
	public void postQuestion(String question) {
		for(SseEmitter s : emitters) {
			try {
				s.send(SseEmitter.event().name("spring").data(question));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
