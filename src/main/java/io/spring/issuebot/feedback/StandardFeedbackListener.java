/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.issuebot.feedback;

import java.time.OffsetDateTime;

import io.spring.issuebot.github.GitHubOperations;
import io.spring.issuebot.github.Issue;

/**
 * Standard implementation of {@link FeedbackListener}.
 *
 * @author Andy Wilkinson
 */
final class StandardFeedbackListener implements FeedbackListener {

	private final GitHubOperations gitHub;

	private final String providedLabel;

	private final String requiredLabel;

	private final String reminderComment;

	private final String closeComment;

	StandardFeedbackListener(GitHubOperations gitHub, String providedLabel,
			String requiredLabel, String reminderComment, String closeComment) {
		this.gitHub = gitHub;
		this.providedLabel = providedLabel;
		this.requiredLabel = requiredLabel;
		this.reminderComment = reminderComment;
		this.closeComment = closeComment;
	}

	@Override
	public void feedbackProvided(Issue issue) {
		this.gitHub.addLabel(issue, this.providedLabel);
		this.gitHub.removeLabel(issue, this.requiredLabel);
	}

	@Override
	public void feedbackRequired(Issue issue, OffsetDateTime requestTime) {
		OffsetDateTime now = OffsetDateTime.now();
		if (requestTime.plusDays(14).isBefore(now)) {
			close(issue);
		}
		else if (requestTime.plusDays(7).isBefore(now)) {
			remind(issue);
		}
	}

	private void close(Issue issue) {
		this.gitHub.addComment(issue, this.closeComment);
		this.gitHub.close(issue);
		this.gitHub.removeLabel(issue, this.requiredLabel);
	}

	private void remind(Issue issue) {
		this.gitHub.addComment(issue, this.reminderComment);
	}

}
