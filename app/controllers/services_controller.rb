class ServicesController < ApplicationController
  before_filter :authenticate_user!, :except => [:create]

  
  #before_filter :require_valid_token, :only => [:service_callback, :service_callback_with_buzz]
  
  def create
    render :text => 'create'
  end
  
  def service_callback
    user = User.find_or_initialize_by_openid_identifier(params['openid.identity'])
    if(user.new_record?)
      user.name = params["openid.ext1.value.firstname"] + ' ' + params["openid.ext1.value.lastname"]
     #user.profile_image_url = AppConfig.server_host + '/images/user_default.png'
     # user.reset_persistence_token
      user.save(false)
    end
    UserSession.create(user)
    
    respond_to do |format|
      format.html do
        render( :template => "/user_google_open_id/successful_login.html.erb",
                :status => 200 )
      end
    end
  end
  
  def service_callback_with_buzz
    mycurrent = current_user
    mycurrent.find_and_merge(:openid_identifier,params['openid.identity'])
    mycurrent.save(false)

    respond_to do |format|
      format.html { redirect_to(authorize_with_googlebuzz_path) }
    end
  end
  
  #-------#
  protected
  #-------#
  
  def require_valid_token
    if params['openid.return_to'].nil? ||
      session[:google_authentication_return_url].nil? ||
       (params['openid.return_to'] != session.delete(:google_authentication_return_url))
      
      respond_to do |format|
        format.html do
          render( :template => "/user_google_open_id/unsuccessful_login.html.erb",
                  :locals => { :error_message => "This request is not issued by google servers" },
                  :status => 500 )
        end
      end 
    end 
  end
#  def create
#    auth = request.env['omniauth.auth']
#    user = User.where(:provider=>auth["provider"],:uid=> auth["uid"]).first unless auth.nil?
#		if user.nil?
#		User.create_with_omniauth(auth)
#		end
#    service[:user_id] = user.id
#    redirect_to root_url, :notice => "Signed in!"
#  end
#def index
#  # get all authentication services assigned to the current user
#  @services = current_user.services.all
#end

#def destroy
#  # remove an authentication service linked to the current user
#  @service = current_user.services.find(params[:id])
#  @service.destroy
#  
#  redirect_to services_path
#end

#def create
#  # get the service parameter from the Rails router
#  params[:service] ? service_route = params[:service] : service_route = 'no service (invalid callback)'

#  # get the full hash from omniauth
#  omniauth = request.env['omniauth.auth']

#  # continue only if hash and parameter exist
#  if omniauth and params[:service]
#    
#    # map the returned hashes to our variables first - the hashes differ for every service
#    if service_route == 'google'
#       omniauth['user_info']['email'] ? email =  omniauth['user_info']['email'] : email = ''
#       omniauth['user_info']['name'] ? name =  omniauth['user_info']['name'] : name = ''
#       omniauth['uid'] ? uid =  omniauth['uid'] : uid = ''
#       omniauth['provider'] ? provider =  omniauth['provider'] : provider = ''
#    else
#      # we have an unrecognized service, just output the hash that has been returned
#      render :text => omniauth.to_yaml
#      #render :text => uid.to_s + " - " + name + " - " + email + " - " + provider
#      return
#    end
#  
#    # continue only if provider and uid exist
#    if uid != '' and provider != ''
#        
#      # nobody can sign in twice, nobody can sign up while being signed in (this saves a lot of trouble)
#      if user_signed_in?
#        
#        # check if user has already signed in using this service provider and continue with sign in process if yes
#        auth = Service.find_by_provider_and_uid(provider, uid)
#        if auth
#          flash[:notice] = 'Signed in successfully via ' + provider.capitalize + '.'
#          sign_in_and_redirect(:user, auth.user)
#        else
#          # check if this user is already registered with this email address; get out if no email has been provided
#          if email != ''
#            # search for a user with this email address
#            existinguser = User.find_by_email(email)
#            if existinguser
#              # map this new login method via a service provider to an existing account if the email address is the same
#              existinguser.services.create(:provider => provider, :uid => uid, :uname => name, :uemail => email)
#              flash[:notice] = 'Sign in via ' + provider.capitalize + ' has been added to your account ' + existinguser.email + '. Signed in successfully!'
#              sign_in_and_redirect(:user, existinguser)
#            else
#              # let's create a new user: register this user and add this authentication method for this user
#              name = name[0, 39] if name.length > 39             # otherwise our user validation will hit us

#              # new user, set email, a random password and take the name from the authentication service
#              user = User.new :email => email, :password => SecureRandom.hex(10), :fullname => name

#              # add this authentication service to our new user
#              user.services.build(:provider => provider, :uid => uid, :uname => name, :uemail => email)

#              # do not send confirmation email, we directly save and confirm the new record
#              user.skip_confirmation!
#              user.save!
#              user.confirm!

#              # flash and sign in
#              flash[:myinfo] = 'Your account on CommunityGuides has been created via ' + provider.capitalize + '. In your profile you can change your personal information and add a local password.'
#              sign_in_and_redirect(:user, user)
#            end
#          else
#            flash[:error] =  service_route.capitalize + ' can not be used to sign-up on CommunityGuides as no valid email address has been provided. Please use another authentication provider or use local sign-up. If you already have an account, please sign-in and add ' + service_route.capitalize + ' from your profile.'
#            redirect_to new_user_session_path
#          end
#        end
#      else
#        # the user is currently signed in
#        
#        # check if this service is already linked to his/her account, if not, add it
#        auth = Service.find_by_provider_and_uid(provider, uid)
#        if !auth
#          current_user.services.create(:provider => provider, :uid => uid, :uname => name, :uemail => email)
#          flash[:notice] = 'Sign in via ' + provider.capitalize + ' has been added to your account.'
#          redirect_to services_path
#        else
#          flash[:notice] = service_route.capitalize + ' is already linked to your account.'
#          redirect_to services_path
#        end  
#      end  
#    else
#      flash[:error] =  service_route.capitalize + ' returned invalid data for the user id.'
#      redirect_to new_user_session_path
#    end
#  else
#    flash[:error] = 'Error while authenticating via ' + service_route.capitalize + '.'
#    redirect_to new_user_session_path
#  end
#end
end
