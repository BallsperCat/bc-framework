package cn.bc.investigate.domain;

import cn.bc.core.EntityImpl;

import javax.persistence.*;

/**
 * 问题项的答案
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_ANSWER")
public class Answer extends EntityImpl {
  private static final long serialVersionUID = 1L;
  private Respond respond; // 对应的作答
  private QuestionItem item; // 作答的问题项
  private int score; // 得分（仅适用于网上考试）
  private String content; // 问答题填写的内容
  private boolean grade = false;// 标识该问题项是否已评分

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "RID", referencedColumnName = "ID")
  public Respond getRespond() {
    return respond;
  }

  public void setRespond(Respond respond) {
    this.respond = respond;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "QID", referencedColumnName = "ID")
  public QuestionItem getItem() {
    return item;
  }

  public void setItem(QuestionItem question) {
    this.item = question;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public boolean isGrade() {
    return grade;
  }

  public void setGrade(boolean grade) {
    this.grade = grade;
  }

}
