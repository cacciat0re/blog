package models

import "time"

type Tag struct {
	ID        int64     `json:"id" gorm:"primary_key;auto_increment"`
	Slug      string    `json:"slug" gorm:"column:slug;size:255;not null"`
	Name      string    `json:"name" gorm:"column:name;size:255;not null"`
	CreatedAt time.Time `json:"created_at" gorm:"column:created_at;not null"`
	UpdatedAt time.Time `json:"updated_at" gorm:"column:updated_at;not null"`
}
