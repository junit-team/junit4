# require
require 'rubygems'

#### INLINE: fixed version of https://github.com/github/upload ####

require 'tempfile'
require 'nokogiri'
require 'httpclient'
require 'stringio'
require 'json'
require 'faster_xml_simple'

module Net
  module GitHub
    class Upload
      VERSION = '0.0.5'
      def initialize params=nil
        @login = params[:login]
        @token = params[:token]

        if @login.empty? or @token.empty?
          raise "login or token is empty"
        end
      end

      def upload info
        unless info[:repos]
          raise "required repository name"
        end
        info[:repos] = @login + '/' + info[:repos] unless info[:repos].include? '/'

        if info[:file]
          file = info[:file]
          unless File.exist?(file) && File.readable?(file)
            raise "file does not exsits or readable"
          end
          info[:name] ||= File.basename(file)
        end
        unless  info[:file] || info[:data]
          raise "required file or data parameter to upload"
        end

        unless info[:name]
          raise "required name parameter for filename with data parameter"
        end

        if info[:replace]
          list_files(info[:repos]).each { |obj|
            next unless obj[:name] == info[:name]
            delete info[:repos], obj[:id]
          }
        elsif list_files(info[:repos]).any?{|obj| obj[:name] == info[:name]}
          raise "file '#{info[:name]}' is already uploaded. please try different name"
        end

        info[:content_type] ||= 'application/octet-stream'
        stat = HTTPClient.post("https://github.com/#{info[:repos]}/downloads", {
          "file_size"    => info[:file] ? File.stat(info[:file]).size : info[:data].size,
          "content_type" => info[:content_type],
          "file_name"    => info[:name],
          "description"  => info[:description] || '',
          "login"        => @login,
          "token"        => @token
        })

        unless stat.code == 200
          raise "Failed to post file info"
        end

        upload_info = JSON.parse(stat.content)
        if info[:file]
          f = File.open(info[:file], 'rb')
        else
          f = Tempfile.open('net-github-upload')
          f << info[:data]
          f.flush
        end
        stat = HTTPClient.post("http://github.s3.amazonaws.com/", [
          ['Filename', info[:name]],
          ['policy', upload_info['policy']],
          ['success_action_status', 201],
          ['key', upload_info['path']],
          ['AWSAccessKeyId', upload_info['accesskeyid']],
          ['Content-Type', upload_info['content_type'] || 'application/octet-stream'],
          ['signature', upload_info['signature']],
          ['acl', upload_info['acl']],
          ['file', f]
        ])
        f.close

        if stat.code == 201
          return FasterXmlSimple.xml_in(stat.content)['PostResponse']['Location']
        else
          raise 'Failed to upload' + extract_error_message(stat)
        end
      end

      def replace info
         upload info.merge( :replace => true )
      end

      def delete_all repos
        unless repos
          raise "required repository name"
        end
        repos = @login + '/' + repos unless repos.include? '/'
        list_files(repos).each { |obj|
          delete repos, obj[:id]
        }
      end

      private

      def extract_error_message(stat)
        # @see http://docs.amazonwebservices.com/AmazonS3/2006-03-01/ErrorResponses.html
        error = FasterXmlSimple.xml_in(stat.content)['Error']
        " due to #{error['Code']} (#{error['Message']})"
      rescue
        ''
      end

      def delete repos, id
        HTTPClient.post("https://github.com/#{repos}/downloads/#{id.gsub( "download_", '')}", {
          "_method"      => "delete",
          "login"        => @login,
          "token"        => @token
        })
      end

      def list_files repos
        raise "required repository name" unless repos
        res = HTTPClient.get_content("https://github.com/#{repos}/downloads", {
          "login" => @login,
          "token" => @token
        })
        Nokogiri::HTML(res).xpath('id("manual_downloads")/li').map do |fileinfo|
          obj = {
            :description => fileinfo.at_xpath('descendant::h4').text,
            :date        => fileinfo.at_xpath('descendant::p/time').attribute('title').text,
            :size        => fileinfo.at_xpath('descendant::p/strong').text,
            :id          => /\d+$/.match(fileinfo.at_xpath('a').attribute('href').text)[0]
          }
          anchor = fileinfo.at_xpath('descendant::h4/a')
          obj[:link] = anchor.attribute('href').text
          obj[:name] = anchor.text
          obj
        end
      end
    end
  end
end

#### END INLINE ####

# setup
login = `git config github.user`.chomp  # your login for github
token = `git config github.token`.chomp # your token for github
repos = 'junit-team/junit'              # your repos name (like 'taberareloo')
gh = Net::GitHub::Upload.new(
  :login => login,
  :token => token
)

version = ARGV[0]

def upload(gh, version, repos, filename, description)
  gh.upload(:repos => repos, 
            :file => "junit#{version}/#{filename}", 
            :description => description)
end

upload(gh, version, repos, "junit-#{version}-src.jar", 'Source jar')
upload(gh, version, repos, "junit-#{version}.jar", 'Basic jar')
upload(gh, version, repos, "junit-dep-#{version}.jar", 'Jar without hamcrest')
upload(gh, version, repos, "junit#{version}.zip", 'Source zip')

# # file upload
# direct_link = gh.upload(
#   :repos => repos,
#   :file  => 'test/test',
#   :description => "test file"
# )
# # direct link is link to Amazon S3.
# # Because GitHub refrection for file changing is async,
# # if you get changed file synchronously, you use this "direct_link"
# 
# # data upload
# # you can define content_type => Amazon S3 Content-Type
# time = Time.now.to_i
# direct_link = gh.upload(
#   :repos => repos,
#   :data  => 'test',
#   :name  => "test_#{time}.txt",
#   :content_type => 'text/plain',
#   :description => "test file2"
# )
# 
# # replace file or data
# # thx id:rngtng !
# direct_link = gh.replace(
#   :repos => repos,
#   :file  => 'test/test',
#   :description => "test file"
# )
# direct_link = gh.replace(
#   :repos => repos,
#   :data  => 'test',
#   :name  => "test_#{time}.txt",
#   :content_type => 'text/plain',
#   :description => "test file2"
# )
