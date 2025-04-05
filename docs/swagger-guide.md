# Hướng dẫn sử dụng Swagger UI trong dự án CareerConnect

## 1. Giới thiệu

Swagger UI là một công cụ mạnh mẽ để khám phá, tương tác và hiểu các API RESTful. Dự án CareerConnect đã tích hợp Swagger để tạo tài liệu API tự động, giúp các frontend developer dễ dàng tìm hiểu và sử dụng các API có sẵn.

## 2. Truy cập Swagger UI

Swagger UI có thể được truy cập thông qua các URL sau:

- **Môi trường local**: http://localhost:8088/swagger-ui.html
- **Môi trường staging/production**: https://[your-api-domain]/swagger-ui.html

## 3. Sử dụng Swagger UI

### 3.1. Tổng quan giao diện

Khi truy cập Swagger UI, bạn sẽ thấy danh sách các API được nhóm theo các tag (như Authentication, Jobs, Candidates, vv.). Mỗi API sẽ hiển thị:
- Tóm tắt (summary) và mô tả chi tiết
- HTTP method (GET, POST, PUT, DELETE)
- URL path
- Các tham số đầu vào
- Cấu trúc response
- Các mã lỗi có thể gặp

### 3.2. Test API trực tiếp

Swagger UI cho phép bạn test API trực tiếp từ giao diện:

1. Chọn API bạn muốn test
2. Click vào nút "Try it out"
3. Nhập các tham số cần thiết
4. Click "Execute"
5. Xem kết quả response (status code, headers, body)

### 3.3. Xác thực (Authentication)

Để test các API yêu cầu xác thực:

1. Đăng nhập thông qua API `/api/auth/login` để nhận JWT token
2. Click vào nút "Authorize" ở trên cùng bên phải của trang
3. Nhập token JWT với định dạng: `Bearer [your_token]`
4. Click "Authorize"

Giờ đây bạn có thể test các API yêu cầu xác thực.

## 4. Các API chính

### 4.1. Authentication APIs
- **Login**: `/api/auth/login` - Đăng nhập người dùng
- **Register**: `/api/auth/register` - Đăng ký tài khoản mới
- **Refresh Token**: `/api/auth/refresh-token` - Làm mới access token
- **Logout**: `/api/auth/logout` - Đăng xuất
- **Google Login**: `/api/auth/google` - Đăng nhập bằng tài khoản Google

### 4.2. Job Management APIs
- **Search Jobs**: `/api/company/jobs/search` - Tìm kiếm công việc
- **View Job Details**: `/api/company/jobs/{id}` - Xem chi tiết công việc
- **Company Jobs**: `/api/company/jobs?companyId={id}` - Xem danh sách công việc của công ty
- **View Applicants**: `/api/company/jobs/{jobId}/view-applicants` - Xem ứng viên đã ứng tuyển

### 4.3. Job Application APIs
- **Apply for Jobs**: `/api/job/apply` - Ứng tuyển công việc
- **Applied Jobs**: `/api/job/applied` - Xem danh sách công việc đã ứng tuyển

### 4.4. Job Search APIs
- **Advanced Search**: `/api/search/search-with-filter` - Tìm kiếm công việc với nhiều bộ lọc

### 4.5. Candidate Profile APIs
- **Get Profile**: `/api/candidate/profile/me` - Lấy thông tin hồ sơ cá nhân
- **Update Profile**: `/api/candidate/profile/me` (PUT) - Cập nhật hồ sơ cá nhân
- **Get CVs**: `/api/candidate/profile/cv` - Lấy danh sách CV
- **Upload CV**: `/api/candidate/profile/cv` (POST) - Tải lên CV mới
- **Delete CV**: `/api/candidate/profile/cv/{cvId}` - Xóa CV
- **View Candidate**: `/api/candidate/profile/{candidateId}` - Xem thông tin ứng viên

## 5. Cách làm việc với các loại API khác nhau

### 5.1. API không có tham số

```javascript
// Ví dụ: Lấy hồ sơ của ứng viên đăng nhập
axios.get('http://api-url/api/candidate/profile/me', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => {
  console.log('Hồ sơ:', response.data);
})
.catch(error => {
  console.error('Lỗi:', error);
});
```

### 5.2. API với tham số Path

```javascript
// Ví dụ: Xem chi tiết công việc với ID = 123
axios.get('http://api-url/api/company/jobs/123', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => {
  console.log('Chi tiết công việc:', response.data);
})
.catch(error => {
  console.error('Lỗi:', error);
});
```

### 5.3. API với tham số Query

```javascript
// Ví dụ: Tìm kiếm công việc với từ khóa "developer"
axios.get('http://api-url/api/company/jobs/search', {
  params: {
    query: 'developer',
    page: 0,
    size: 10
  }
})
.then(response => {
  console.log('Kết quả tìm kiếm:', response.data);
})
.catch(error => {
  console.error('Lỗi:', error);
});
```

### 5.4. API với Body JSON

```javascript
// Ví dụ: Đăng ký tài khoản mới
axios.post('http://api-url/api/auth/register', {
  email: 'user@example.com',
  password: 'password123',
  fullName: 'Nguyen Van A',
  role: 'CANDIDATE'
})
.then(response => {
  console.log('Đăng ký thành công:', response.data);
})
.catch(error => {
  console.error('Lỗi đăng ký:', error);
});
```

### 5.5. API upload file (Multipart/form-data)

```javascript
// Ví dụ: Upload CV
const formData = new FormData();
formData.append('cvName', 'My Resume 2025');
formData.append('file', fileObject); // fileObject từ input file

axios.post('http://api-url/api/candidate/profile/cv', formData, {
  headers: {
    'Content-Type': 'multipart/form-data',
    'Authorization': `Bearer ${token}`
  }
})
.then(response => {
  console.log('Upload CV thành công:', response.data);
})
.catch(error => {
  console.error('Lỗi upload CV:', error);
});
```

## 6. Tích hợp với Frontend Frameworks

### 6.1. React với Axios

```javascript
import { useState, useEffect } from 'react';
import axios from 'axios';

function JobList() {
  const [jobs, setJobs] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    const fetchJobs = async () => {
      setIsLoading(true);
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://api-url/api/company/jobs/search', {
          headers: {
            'Authorization': `Bearer ${token}`
          },
          params: {
            query: '',
            page: 0,
            size: 10
          }
        });
        
        setJobs(response.data.result.content);
        setError(null);
      } catch (err) {
        setError('Không thể tải danh sách công việc');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchJobs();
  }, []);
  
  return (
    <div>
      {isLoading && <p>Đang tải...</p>}
      {error && <p className="error">{error}</p>}
      <ul>
        {jobs.map(job => (
          <li key={job.id}>
            <h3>{job.title}</h3>
            <p>{job.company.name}</p>
            <p>{job.location}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
```

### 6.2. React Query

```javascript
import { useQuery, useMutation, QueryClient, QueryClientProvider } from 'react-query';
import axios from 'axios';

// Tạo API client
const api = axios.create({
  baseURL: 'http://api-url',
});

// Thêm interceptor cho authorization
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Hook để lấy danh sách công việc
function useJobs(query, page = 0, size = 10) {
  return useQuery(
    ['jobs', query, page, size],
    () => api.get('/api/company/jobs/search', { 
      params: { query, page, size } 
    }).then(res => res.data.result),
    {
      keepPreviousData: true,
      staleTime: 30000,
    }
  );
}

// Hook để ứng tuyển công việc
function useApplyJob() {
  return useMutation(
    (jobApplication) => api.post('/api/job/apply', jobApplication)
      .then(res => res.data)
  );
}

// Ví dụ sử dụng
function JobList() {
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  
  const { 
    data, 
    isLoading, 
    error,
    isPreviousData
  } = useJobs(searchQuery, currentPage);
  
  return (
    <div>
      <input 
        value={searchQuery}
        onChange={e => setSearchQuery(e.target.value)}
        placeholder="Tìm kiếm công việc..."
      />
      
      {isLoading && <p>Đang tải...</p>}
      {error && <p>Lỗi: {error.message}</p>}
      
      <ul>
        {data?.content.map(job => (
          <li key={job.id}>{job.title}</li>
        ))}
      </ul>
      
      <button
        onClick={() => setCurrentPage(old => Math.max(old - 1, 0))}
        disabled={currentPage === 0}
      >
        Trang trước
      </button>
      <span>Trang {currentPage + 1}</span>
      <button
        onClick={() => {
          if (!isPreviousData && data?.hasNext) {
            setCurrentPage(old => old + 1);
          }
        }}
        disabled={isPreviousData || !data?.hasNext}
      >
        Trang sau
      </button>
    </div>
  );
}
```

## 7. Xử lý lỗi và Best Practices

### 7.1. Xử lý lỗi API

```javascript
axios.get('/api/some-endpoint')
  .then(response => {
    // Xử lý dữ liệu thành công
  })
  .catch(error => {
    if (error.response) {
      // Lỗi server với status code
      switch (error.response.status) {
        case 400:
          console.error('Dữ liệu không hợp lệ:', error.response.data);
          break;
        case 401:
          console.error('Chưa xác thực');
          // Redirect tới trang đăng nhập
          break;
        case 403:
          console.error('Không có quyền truy cập');
          break;
        case 404:
          console.error('Không tìm thấy tài nguyên');
          break;
        case 500:
          console.error('Lỗi server:', error.response.data);
          break;
        default:
          console.error('Lỗi không xác định:', error.response.status);
      }
    } else if (error.request) {
      // Không nhận được response
      console.error('Không nhận được phản hồi từ server');
    } else {
      // Lỗi khi thiết lập request
      console.error('Lỗi:', error.message);
    }
  });
```

### 7.2. Xử lý làm mới token

```javascript
// Interceptor xử lý token hết hạn và tự động làm mới
api.interceptors.response.use(
  response => response, 
  async error => {
    const originalRequest = error.config;
    
    // Kiểm tra lỗi 401 và chưa thử làm mới token
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Lấy refresh token từ cookie
        const refreshToken = getCookie('refreshToken');
        
        if (!refreshToken) {
          // Không có refresh token, chuyển đến trang đăng nhập
          window.location.href = '/login';
          return Promise.reject(error);
        }
        
        // Gọi API làm mới token
        const res = await axios.post('/api/auth/refresh-token', {}, {
          withCredentials: true // Để gửi cookie
        });
        
        const newToken = res.data.result.token;
        
        // Lưu token mới
        localStorage.setItem('token', newToken);
        
        // Thiết lập lại Authorization header và thử lại request
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        // Lỗi khi làm mới token, đăng xuất và chuyển đến trang đăng nhập
        localStorage.removeItem('token');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

// Hàm lấy cookie
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}
```

## 8. Hỗ trợ và liên hệ

Nếu có bất kỳ câu hỏi hoặc vấn đề về API, vui lòng liên hệ với backend team qua:
- Email: backend@careerconnect.com
- Slack channel: #api-support

---

**Happy coding!**