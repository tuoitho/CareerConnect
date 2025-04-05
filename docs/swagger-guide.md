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
- Login: `/api/auth/login`
- Register: `/api/auth/register`
- Refresh Token: `/api/auth/refresh-token`
- Logout: `/api/auth/logout`
- Google Login: `/api/auth/google`

### 4.2. Jobs APIs
- Search Jobs: `/api/company/jobs/search`
- View Job Details: `/api/company/jobs/{id}`
- Apply for Jobs: `/api/job/apply`

### 4.3. Profile APIs
- Get Candidate Profile: `/api/candidate/profile/me`
- Update Profile: `/api/candidate/profile/update`
- Upload CV: `/api/candidate/profile/cv/upload`

## 5. Tích hợp với Frontend

### 5.1. Sử dụng Axios

Ví dụ về cách gọi API với Axios:

```javascript
// Ví dụ đăng nhập
axios.post('http://api-url/api/auth/login', {
  email: 'user@example.com',
  password: 'password'
}, {
  params: {
    tk: 'turnstileToken'
  }
})
.then(response => {
  // Lưu token
  localStorage.setItem('token', response.data.token);
})
.catch(error => {
  console.error('Lỗi đăng nhập:', error);
});

// Ví dụ gọi API có bảo mật
axios.get('http://api-url/api/some-secured-endpoint', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
})
.then(response => {
  // Xử lý dữ liệu
})
.catch(error => {
  // Xử lý lỗi
});
```

### 5.2. Tích hợp với React Query

```javascript
import { useQuery, useMutation, QueryClient } from 'react-query';
import axios from 'axios';

// Set up axios với base URL và interceptors
const api = axios.create({
  baseURL: 'http://api-url',
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Ví dụ hook để tìm kiếm công việc
export const useSearchJobs = (searchParams) => {
  return useQuery(
    ['searchJobs', searchParams],
    () => api.get('/api/company/jobs/search', { params: searchParams })
      .then(res => res.data),
    {
      keepPreviousData: true,
      staleTime: 5000,
    }
  );
};

// Ví dụ hook để apply công việc
export const useApplyJob = () => {
  return useMutation(
    (applicationData) => api.post('/api/job/apply', applicationData)
      .then(res => res.data)
  );
};
```

## 6. Lưu ý và Best Practices

1. **Luôn kiểm tra tài liệu**: Swagger UI luôn được cập nhật theo code, vì vậy hãy sử dụng nó như nguồn thông tin chính thức về API.

2. **Xử lý lỗi**: Luôn xử lý các mã lỗi từ API, đặc biệt là 401 (Unauthorized), 403 (Forbidden), và 500 (Server Error).

3. **Token management**: Lưu ý về việc làm mới token khi hết hạn và xử lý logout.

4. **API versioning**: Kiểm tra phiên bản API nếu có thay đổi trong tương lai.

5. **Rate limiting**: Lưu ý về giới hạn số lượng request trong một khoảng thời gian nếu có.

## 7. Hỗ trợ

Nếu có bất kỳ câu hỏi hoặc vấn đề về API, vui lòng liên hệ với backend team qua:
- Email: backend@careerconnect.com
- Slack channel: #api-support

---

**Happy coding!**