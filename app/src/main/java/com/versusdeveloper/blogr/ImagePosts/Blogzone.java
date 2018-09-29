package com.versusdeveloper.blogr.ImagePosts;

public class Blogzone {

 private String title, desc, imageUrl;

 public Blogzone(String title, String desc, String imageUrl) {

 this.title = title;
 this.desc = desc;
 this.imageUrl=imageUrl;

 }

 // create an empty constructor
 public Blogzone() {
 }

 public void setImageUrl(String imageUrl) {
 this.imageUrl = imageUrl;
 }

 public void setTitle(String title) {
 this.title = title;
 }

 public void setDesc(String desc) {
 this.desc = desc;
 }

 public String getImageUrl() {
 return imageUrl;
 }

 public String getTitle() {
 return title;
 }

 public String getDesc() {
 return desc;
 }
}