package models

type Category struct {
	ID           int64                 `json:"id" gorm:"primary_key;auto_increment"`
	Slug         string                `json:"slug" gorm:"column:slug;size:255;not null"`
	ParentID     int64                 `json:"parent_id" gorm:"column:parent_id;not null"`
	Parent       *Category             `json:"parent" gorm:"foreignkey:ParentID"`
	Translations []CategoryTranslation `json:"translations" gorm:"foreignkey:Slug"`
}

type CategoryTranslation struct {
	ID         int64  `json:"id" gorm:"primary_key;auto_increment"`
	CategoryID int64  `json:"category_id" gorm:"column:category_id;not null"`
	Language   string `json:"language" gorm:"column:language;size:255;not null"`
	Content    string `json:"content" gorm:"column:content;not null"`
}
