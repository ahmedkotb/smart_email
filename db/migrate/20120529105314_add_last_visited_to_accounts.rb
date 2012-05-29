class AddLastVisitedToAccounts < ActiveRecord::Migration
  def change
    add_column :accounts, :last_visited, :datetime

  end
end
