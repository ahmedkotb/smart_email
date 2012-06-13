class AccountsController < ApplicationController
  # GET /accounts
  # GET /accounts.json
	require "gmail"
  def index
    @accounts = current_user.accounts.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @accounts }
    end
  end

  # GET /accounts/1
  # GET /accounts/1.json
  def show
    @account = current_user.accounts.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @account }
    end
  end

  # GET /accounts/new
  # GET /accounts/new.json
  def new
    @account = current_user.accounts.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @account }
      format.xml { render xml: @account }
    end
  end

  # GET /accounts/1/edit
  def edit
    @account = current_user.accounts.find(params[:id])
  end

  # POST /accounts
  # POST /accounts.json
  def create
    @account = current_user.accounts.new(params[:account])

    respond_to do |format|
      if @account.save
        require 'net/http'
        xml = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
        xml += '<account>'
        xml += '<email>' + @account.username + '@' + @account.domain + '</email>'
        xml += '<token>' + @account.password + '</token>'
        xml += '</account>'

        uri = URI.parse('http://localhost:8080/smart_email/rest/service/provider/register')
        request = Net::HTTP::Post.new(uri.request_uri)
        request.body = xml
        puts "request = #{request}"
        response = http.request(request)
        puts "response = #{response}"


        format.html { redirect_to @account, notice: 'Account was successfully created.' }
        format.json { render json: @account, status: :created, location: @account }
        format.xml { render json: @account, status: :created, location: @account }
      else
        format.html { render action: "new" }
        format.json { render json: @account.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /accounts/1
  # PUT /accounts/1.json
  def update
    @account = current_user.accounts.find(params[:id])

    respond_to do |format|
      if @account.update_attributes(params[:account])
        format.html { redirect_to @account, notice: 'Account was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @account.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /accounts/1
  # DELETE /accounts/1.json
  def destroy
    @account = current_user.accounts.find(params[:id])
    @account.destroy

    respond_to do |format|
      format.html { redirect_to accounts_url }
      format.json { head :no_content }
    end
  end
	
	def labels
  @account = current_user.accounts.find(params[:id])

	@gmail = Gmail.connect(@account.username,@account.password)
  @inbox_count = @gmail.inbox.count()
  @unread_count = @gmail.inbox.count(:unread)
  @read_count = @gmail.inbox.count(:read)
	@account_labels = Array.new 
	@counts = Array.new 
	@account_labels = @gmail.labels.all

	@account_labels.each do |label|
		if !label.include? "[Gmail]" 
		@counts << @gmail.label(label).count
		else
		@counts <<""
		end
	end
	@gmail.logout

	respond_to do |format|
  	format.html # labels.html.erb
    format.json { render json: @account_labels }
  end
	end
end
