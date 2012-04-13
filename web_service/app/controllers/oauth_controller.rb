class OauthController < ApplicationController

  def authenticate
      oauth_token = params[:oauth_token]
      oauth_verifier = params[:oauth_verifier]
      
      @user = User.find_by_oauth_token(oauth_token)
      if !@user
          # Do something appropriate, such as a 404
      else
          begin
              oauth_tokens = get_access_token(oauth_token, @user.oauth_token_secret, oauth_verifier)
              @user.oauth_token_secret = oauth_token
							@user.oauth_token_secret = oauth_token_secret
							@user.save
          rescue Exception => e
              # Something went wrong, or user did not give you permissions on Gmail
              # Do something appropriate, potentially try again?
              flash[:error] = "There was an error while authenticating with Gmail. Please try again."
          end          
          redirect_to root_url
      end
  end
  
end
