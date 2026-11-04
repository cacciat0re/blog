package model

type Category struct {
	ID           int64                 `json:"id" gorm:"primaryKey;autoIncrement"`
	Slug         string                `json:"slug" gorm:"column:slug;size:255;not null;uniqueIndex:uq_categories_slug"`
	ParentID     *int64                `json:"parent_id" gorm:"column:parent_id;index"`
	Parent       *Category             `json:"parent" gorm:"foreignKey:ParentID"`
	Translations []CategoryTranslation `json:"translations" gorm:"foreignKey:CategoryID"`
}

type CategoryTranslation struct {
	ID         int64  `json:"id" gorm:"primaryKey;autoIncrement"`
	CategoryID int64  `json:"category_id" gorm:"column:category_id;not null"`
	Language   string `json:"language" gorm:"column:language;size:2;not null"`
	Content    string `json:"content" gorm:"column:content;size:255;not null"`
}
