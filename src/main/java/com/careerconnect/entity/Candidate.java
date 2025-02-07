package com.careerconnect.entity;

import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Candidate extends User {
    private String fullname;
    private String avatar;
    private String phone;
    private String email;
    private String bio;


    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Education> educations;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experience> experiences;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CV> cvs;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications;


    public void assignEducations(Set<Education> newEducations) {
        if (this.educations == null) {
            this.educations = new HashSet<>();
        }

        // Xóa các phần tử cũ không còn trong newEducations (vì orphanRemoval = true sẽ xóa chúng)
        this.educations.removeIf(existingEdu ->
                newEducations.stream().noneMatch(newEdu ->
                        newEdu.getEducationId() != null && newEdu.getEducationId().equals(existingEdu.getEducationId())
                )
        );


        // Cập nhật hoặc thêm mới các phần tử từ newEducations
        newEducations.forEach(newEdu -> {
            if (newEdu.getEducationId() == null) {
                newEdu.setCandidate(this); // Đảm bảo liên kết Candidate
                this.educations.add(newEdu);
                return;
            }
            this.educations.stream()
                    .filter(edu -> edu.getEducationId().equals(newEdu.getEducationId()))
                    .findFirst().ifPresentOrElse(
                            existingEdu -> {
                                BeanUtils.copyProperties(newEdu, existingEdu);
                                existingEdu.setCandidate(this); // Đảm bảo liên kết Candidate
                            },
                            () -> {
                                throw new AppException(ErrorCode.RESOURCE_MUST_BE_CREATED_OR_MODIFIED);
                            }
                    );
        });
    }

    public void assignExperiences(Set<Experience> newExperiences) {
        if (this.experiences == null) {
            this.experiences = new HashSet<>();
        }

        this.experiences.removeIf(existingExp ->
                newExperiences.stream().noneMatch(newExp ->
                        newExp.getExperienceId() != null && newExp.getExperienceId().equals(existingExp.getExperienceId())
                )
        );

        newExperiences.forEach(newExp -> {
            if (newExp.getExperienceId() == null) {
                newExp.setCandidate(this);
                this.experiences.add(newExp);
                return;
            }
            this.experiences.stream()
                    .filter(exp -> exp.getExperienceId().equals(newExp.getExperienceId()))
                    .findFirst().ifPresentOrElse(
                            existingExp -> {
                                BeanUtils.copyProperties(newExp, existingExp, "candidate"); // Exclude candidate
                                existingExp.setCandidate(this);
                            },
                            () -> {
                                throw new AppException(ErrorCode.RESOURCE_MUST_BE_CREATED_OR_MODIFIED);
                            }
                    );
        });
    }

    public void assignCVs(Set<CV> newCVs) {
        if (this.cvs == null) {
            this.cvs = new HashSet<>();
        }

        this.cvs.removeIf(existingCV ->
                newCVs.stream().noneMatch(newCV ->
                        newCV.getCvId() != null &&
                        newCV.getCvId().equals(existingCV.getCvId())
                )
        );

        newCVs.forEach(newCV -> {
            if (newCV.getCvId() == null) {
                newCV.setCandidate(this);
                this.cvs.add(newCV);
            } else
                this.cvs.stream()
                        .filter(cv -> cv.getCvId().equals(newCV.getCvId()))
                        .findFirst().ifPresentOrElse(
                                existingCV -> {
                                    BeanUtils.copyProperties(newCV, existingCV, "candidate"); // Exclude candidate
                                    existingCV.setCandidate(this);
                                },
                                () -> {
                                    throw new AppException(ErrorCode.RESOURCE_MUST_BE_CREATED_OR_MODIFIED);
                                }
                        );
        });
    }

    public void addCV(CV cv) {
        if (this.cvs == null) {
            this.cvs = new HashSet<>();
        }
        cv.setCandidate(this);
        this.cvs.add(cv);
    }

    public void removeCV(CV cv) {
        if (this.cvs == null) {
            return;
        }
        this.cvs.remove(cv);
    }
}
