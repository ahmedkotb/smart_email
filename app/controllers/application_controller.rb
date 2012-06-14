class ApplicationController < ActionController::Base
  protect_from_forgery

  protected
  def sendPostRequest(url, body, content_type)
    uri = URI.parse(url)
    http = Net::HTTP.new(uri.host, uri.port)
    request = Net::HTTP::Post.new(uri.request_uri)
    request.body = body
    request['Content-Type'] = content_type
    return http.request(request)
  end

  def sendDeleteRequest(url)
    puts '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>'
    puts 'horraaaaaaay'
    uri = URI.parse(url)
    http = Net::HTTP.new(uri.host, uri.port)
    request = Net::HTTP::DELETE.new(uri.request_uri)
    return http.request(request)
  end
end
