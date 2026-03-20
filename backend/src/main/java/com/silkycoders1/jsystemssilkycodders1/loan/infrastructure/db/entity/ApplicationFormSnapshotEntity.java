package com.silkycoders1.jsystemssilkycodders1.loan.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "application_form_snapshots")
public class ApplicationFormSnapshotEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "form_version", nullable = false)
	private String formVersion;

	@Column(name = "prefilled_json", nullable = false, columnDefinition = "TEXT")
	private String prefilledJson;

	@Column(name = "submitted_json", nullable = false, columnDefinition = "TEXT")
	private String submittedJson;

	@Column(name = "validation_errors_json", nullable = false, columnDefinition = "TEXT")
	private String validationErrorsJson;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	public static ApplicationFormSnapshotEntity create(Long applicationId, String formVersion, String prefilledJson, String submittedJson, String validationErrorsJson) {
		var entity = new ApplicationFormSnapshotEntity();
		entity.applicationId = applicationId;
		entity.formVersion = formVersion;
		entity.prefilledJson = prefilledJson;
		entity.submittedJson = submittedJson;
		entity.validationErrorsJson = validationErrorsJson;
		entity.createdAt = OffsetDateTime.now();
		return entity;
	}
}
