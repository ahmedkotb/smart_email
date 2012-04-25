class AddUserIdToAccounts < ActiveRecord::Migration
  def change
		add_column :accounts, :user_id, :integer
    add_index  :accounts, :user_id
  end
end
