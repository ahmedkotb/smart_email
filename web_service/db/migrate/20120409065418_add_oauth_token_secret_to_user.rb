class AddOauthTokenSecretToUser < ActiveRecord::Migration
  def change
    add_column :users, :oauth_token_secret, :string

  end
end
